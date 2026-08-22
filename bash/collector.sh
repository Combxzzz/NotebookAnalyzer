#!/bin/bash

# ==============================================================================
# 1. INSTALAÇÃO E ATUALIZAÇÃO DE DEPENDÊNCIAS
# ==============================================================================
echo "Atualizando repositórios e instalando dependências..."
sudo apt update
sudo apt install -y jq curl smartmontools lm-sensors pciutils dmidecode

echo "Coletando informações do computador..."

# ==============================================================================
# 2. INFORMAÇÕES DO COMPUTADOR (Tratativa para Nome Comercial)
# ==============================================================================
manufacturer=$(sudo dmidecode -s system-manufacturer 2>/dev/null | xargs)
product_name=$(sudo dmidecode -s system-product-name 2>/dev/null | xargs)
product_family=$(sudo dmidecode -s system-family 2>/dev/null | xargs)
product_version=$(sudo dmidecode -s system-version 2>/dev/null | xargs)
serial=$(sudo dmidecode -s system-serial-number 2>/dev/null | xargs)

# Tenta capturar do /sys caso o dmidecode retorne 'Not Specified'
[ -z "$product_family" ] || [ "$product_family" == "Not Specified" ] && product_family=$(cat /sys/class/dmi/id/product_family 2>/dev/null | xargs)
[ -z "$product_version" ] || [ "$product_version" == "Not Specified" ] && product_version=$(cat /sys/class/dmi/id/product_version 2>/dev/null | xargs)

# Lógica para priorizar o nome comercial (ex: ThinkPad L14 Gen 2) e incluir o número do modelo
if [ -n "$product_family" ] && [ "$product_family" != "Not Specified" ]; then
    model="$product_family ($product_name)"
elif [ -n "$product_version" ] && [ "$product_version" != "Not Specified" ] && [[ "$product_version" =~ ThinkPad|Lenovo ]]; then
    model="$product_version ($product_name)"
else
    model="$product_name"
fi

[ -z "$manufacturer" ] && manufacturer="N/A"
[ -z "$model" ] && model="N/A"
[ -z "$serial" ] && serial="N/A"

# ==============================================================================
# 3. INFORMAÇÕES DA CPU E TEMPERATURA
# ==============================================================================
cpu_model=$(lscpu | grep "Model name" | cut -d ':' -f 2 | xargs)
cpu_arch=$(lscpu | grep "Architecture" | cut -d ':' -f 2 | xargs)
cpu_cores=$(lscpu | grep "^Core(s) per socket:" | cut -d ':' -f 2 | xargs)
cpu_threads=$(lscpu | grep "^CPU(s):" | cut -d ':' -f 2 | xargs)

# Leitura da temperatura ajustada para o formato do sensors (Package id 0: +XX.X°C)
cpu_temp=$(sensors 2>/dev/null | grep -i "Package id 0" | awk '{print $4}' | tr -d '+°C' | cut -d'.' -f1)

[ -z "$cpu_model" ] && cpu_model="N/A"
[ -z "$cpu_arch" ] && cpu_arch="N/A"
[[ ! "$cpu_cores" =~ ^[0-9]+$ ]] && cpu_cores="null"
[[ ! "$cpu_threads" =~ ^[0-9]+$ ]] && cpu_threads="null"
[[ ! "$cpu_temp" =~ ^[0-9]+$ ]] && cpu_temp="null"

# ==============================================================================
# 4. INFORMAÇÕES DA MEMÓRIA RAM
# ==============================================================================
ram_gb=$(free -g | awk '/Mem:/ {print $2}')
ram_type=$(sudo dmidecode --type memory 2>/dev/null | grep -i "Type:" | grep -vE "Unknown|None" | head -n 1 | awk '{print $2}')
ram_speed=$(sudo dmidecode --type memory 2>/dev/null | grep -i "Configured Memory Speed:" | grep -v "Unknown" | head -n 1 | awk '{print $4}')

[[ ! "$ram_gb" =~ ^[0-9]+$ ]] && ram_gb="null"
[ -z "$ram_type" ] && ram_type="N/A"
[[ ! "$ram_speed" =~ ^[0-9]+$ ]] && ram_speed="null"

# ==============================================================================
# 5. INFORMAÇÕES DA GPU
# ==============================================================================
gpu_model=$(lspci | grep -Ei "vga|3d|display" | cut -d ':' -f 3 | xargs)
[ -z "$gpu_model" ] && gpu_model="N/A"

# ==============================================================================
# 6. INFORMAÇÕES DO ARMAZENAMENTO (SMART)
# ==============================================================================
# Ignora o pendrive do Ubuntu Live e seleciona o primeiro disco interno disponível.
target_disk=$(lsblk -d -n -o NAME,TYPE,TRAN | awk '$2 == "disk" && $3 != "usb" { print $1; exit }')

if [ -n "$target_disk" ]; then
    disk_path="/dev/$target_disk"
    
    disk_model=$(sudo smartctl -i "$disk_path" 2>/dev/null | grep -i "Device Model\|Model Number" | cut -d ':' -f 2 | xargs)
    disk_serial=$(sudo smartctl -i "$disk_path" 2>/dev/null | grep -i "Serial Number" | cut -d ':' -f 2 | xargs)
    disk_size=$(lsblk -d -n -o SIZE "$disk_path" | xargs)
    
    rotational=$(cat /sys/block/$target_disk/queue/rotational 2>/dev/null)
    if [ "$rotational" == "0" ]; then
        disk_type="SSD"
    elif [ "$rotational" == "1" ]; then
        disk_type="HDD"
    else
        disk_type="UNKNOWN"
    fi
    
    disk_health=$(sudo smartctl -H "$disk_path" 2>/dev/null | grep -i "test result\|overall-health" | cut -d ':' -f 2 | xargs)
    disk_hours=$(sudo smartctl -A "$disk_path" 2>/dev/null | grep -i "Power_On_Hours\|Power On Hours" | awk '{print $NF}' | tr -d ',')
    
    [ -z "$disk_model" ] && disk_model="N/A"
    [ -z "$disk_serial" ] && disk_serial="N/A"
    [ -z "$disk_size" ] && disk_size="N/A"
    [ -z "$disk_health" ] && disk_health="UNKNOWN"
    [[ ! "$disk_hours" =~ ^[0-9]+$ ]] && disk_hours="null"
else
    disk_model="N/A"
    disk_serial="N/A"
    disk_size="N/A"
    disk_type="N/A"
    disk_health="N/A"
    disk_hours="null"
fi

# ==============================================================================
# 7. INFORMAÇÕES DA BATERIA
# ==============================================================================
bat_path=""
for candidate in /sys/class/power_supply/BAT*; do
    if [ -d "$candidate" ]; then
        bat_path="$candidate"
        break
    fi
done

if [ -n "$bat_path" ]; then
    bat_cycles=$(cat "$bat_path/cycle_count" 2>/dev/null)

    if [ -r "$bat_path/energy_full" ] && [ -r "$bat_path/energy_full_design" ]; then
        full_capacity=$(cat "$bat_path/energy_full" 2>/dev/null)
        design_capacity=$(cat "$bat_path/energy_full_design" 2>/dev/null)
        capacity_unit="uWh"
    elif [ -r "$bat_path/charge_full" ] && [ -r "$bat_path/charge_full_design" ]; then
        full_capacity=$(cat "$bat_path/charge_full" 2>/dev/null)
        design_capacity=$(cat "$bat_path/charge_full_design" 2>/dev/null)
        capacity_unit="uAh"
    else
        full_capacity="null"
        design_capacity="null"
        capacity_unit="N/A"
    fi
    
    [[ ! "$bat_cycles" =~ ^[0-9]+$ ]] && bat_cycles="null"
    [[ ! "$full_capacity" =~ ^[0-9]+$ ]] && full_capacity="null"
    [[ ! "$design_capacity" =~ ^[0-9]+$ ]] && design_capacity="null"

    if [ "$full_capacity" != "null" ] && [ "$design_capacity" != "null" ] && [ "$design_capacity" -gt 0 ]; then
        bat_health=$(( (full_capacity * 100) / design_capacity ))
    else
        bat_health="null"
    fi
else
    bat_health="null"
    bat_cycles="null"
    full_capacity="null"
    design_capacity="null"
    capacity_unit="N/A"
fi

# ==============================================================================
# 8. GERAÇÃO DO JSON COM JQ
# ==============================================================================
jq -n \
  --arg manufacturer "$manufacturer" \
  --arg model "$model" \
  --arg serial "$serial" \
  --arg cpu_model "$cpu_model" \
  --arg cpu_arch "$cpu_arch" \
  --argjson cpu_cores "${cpu_cores:-null}" \
  --argjson cpu_threads "${cpu_threads:-null}" \
  --argjson cpu_temp "${cpu_temp:-null}" \
  --argjson ram_gb "${ram_gb:-null}" \
  --arg ram_type "$ram_type" \
  --argjson ram_speed "${ram_speed:-null}" \
  --arg gpu_model "$gpu_model" \
  --arg disk_model "$disk_model" \
  --arg disk_serial "$disk_serial" \
  --arg disk_size "$disk_size" \
  --arg disk_type "$disk_type" \
  --arg disk_health "$disk_health" \
  --argjson disk_hours "${disk_hours:-null}" \
  --argjson bat_health "${bat_health:-null}" \
  --argjson bat_cycles "${bat_cycles:-null}" \
  --argjson full_capacity "${full_capacity:-null}" \
  --argjson design_capacity "${design_capacity:-null}" \
  --arg capacity_unit "$capacity_unit" \
  '{
    computer: {
      manufacturer: $manufacturer,
      model: $model,
      serial_number: $serial
    },
    cpu: {
      model: $cpu_model,
      architecture: $cpu_arch,
      cores: $cpu_cores,
      threads: $cpu_threads,
      temperature_celsius: $cpu_temp
    },
    memory: {
      total_gb: $ram_gb,
      type: $ram_type,
      speed_mhz: $ram_speed
    },
    gpu: {
      model: $gpu_model
    },
    storage: {
      model: $disk_model,
      serial_number: $disk_serial,
      size: $disk_size,
      type: $disk_type,
      health_status: $disk_health,
      power_on_hours: $disk_hours
    },
    battery: {
      health_percentage: $bat_health,
      cycle_count: $bat_cycles,
      full_capacity: $full_capacity,
      design_capacity: $design_capacity,
      capacity_unit: $capacity_unit
    }
  }' > result.json

echo "Processo finalizado! O arquivo result.json foi gerado com sucesso:"
cat result.json
