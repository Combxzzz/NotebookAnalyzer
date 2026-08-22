# 1.0 — BASH e preparação

O primeiro passo do projeto consiste em preparar um ambiente Linux padronizado e desenvolver um script Bash responsável por coletar as principais informações de hardware e conservação dos notebooks.

A coleta será realizada utilizando um **Ubuntu Live**, inicializado através de um pendrive. Dessa forma, o sistema operacional instalado no notebook não interfere diretamente na coleta das informações.

O objetivo desta etapa é executar um único script em cada computador e gerar um arquivo `result.json` contendo todas as informações coletadas em um formato padronizado.

O fluxo atual é:

```text
Notebook
   ↓
Ubuntu Live
   ↓
collector.sh
   ↓
Instalação das dependências
   ↓
Coleta das informações
   ↓
Tratamento dos dados
   ↓
Geração do JSON
   ↓
result.json
```

---

# 1.1 — Sistema operacional

Será utilizado o **Ubuntu Desktop LTS** como ambiente de execução do script.

O Ubuntu será inicializado no modo **Live**, através de um pendrive, sem a necessidade de instalar o sistema no armazenamento interno do notebook.

A utilização de um Live Linux permite que todos os computadores sejam analisados utilizando um ambiente semelhante, independentemente do sistema operacional originalmente instalado.

Isso é importante para manter os testes padronizados.

```text
Notebook A ──┐
Notebook B ──┤
Notebook C ──┼──► Ubuntu Live ──► collector.sh
Notebook D ──┤
Notebook E ──┘
```

---

# 1.2 — Instalação e atualização das dependências

Ao iniciar o script, o primeiro passo é atualizar os repositórios do Ubuntu:

```bash
sudo apt update -y
```

O `apt update` atualiza a lista de pacotes disponíveis nos repositórios configurados no sistema.

Depois disso, o script instala todas as ferramentas necessárias:

```bash
sudo apt install -y \
    jq \
    curl \
    smartmontools \
    lm-sensors \
    upower \
    pciutils \
    dmidecode \
    bc
```

As ferramentas utilizadas atualmente são:

| Ferramenta | Utilização |
|---|---|
| `jq` | Geração e manipulação do JSON |
| `curl` | Futuramente utilizado para enviar o JSON para a API |
| `smartmontools` | Obtenção de informações SMART do armazenamento |
| `lm-sensors` | Leitura de sensores e temperatura |
| `upower` | Informações relacionadas à energia e bateria |
| `pciutils` | Identificação de dispositivos PCI, como GPU |
| `dmidecode` | Informações do hardware através do DMI/SMBIOS |
| `bc` | Operações matemáticas no Bash |

A saída dos comandos de atualização e instalação é redirecionada para `/dev/null` para evitar que o terminal fique cheio de mensagens durante a execução:

```bash
sudo apt update -y > /dev/null 2>&1
sudo apt install -y ... > /dev/null 2>&1
```

---

# 1.3 — Identificação do computador

A primeira informação coletada é a identificação física do notebook.

São utilizados os seguintes comandos:

```bash
sudo dmidecode -s system-manufacturer
```

Obtém o fabricante do computador.

```bash
sudo dmidecode -s system-product-name
```

Obtém o nome do produto/modelo identificado pelo firmware.

```bash
sudo dmidecode -s system-family
```

Obtém a família do produto.

```bash
sudo dmidecode -s system-version
```

Obtém a versão do produto.

```bash
sudo dmidecode -s system-serial-number
```

Obtém o número de série.

Essas informações são importantes para identificar cada notebook no sistema.

## Tratamento do nome comercial

Um problema encontrado durante a coleta é que o nome retornado pelo `dmidecode` nem sempre corresponde ao nome comercial que queremos utilizar.

Por isso, o script também consulta:

```bash
/sys/class/dmi/id/product_family
```

e:

```bash
/sys/class/dmi/id/product_version
```

Caso o `dmidecode` retorne valores vazios ou `Not Specified`, esses arquivos são utilizados como alternativa.

O script também combina essas informações para tentar produzir um nome comercial mais útil.

Por exemplo:

```text
ThinkPad L14 Gen 2 (20X2SBGJ00)
```

em vez de utilizar somente:

```text
20X2SBGJ00
```

---

# 1.4 — Informações da CPU

As informações do processador são obtidas através do comando:

```bash
lscpu
```

O script extrai especificamente:

- Modelo;
- Arquitetura;
- Núcleos;
- Threads.

### Modelo

```bash
lscpu | grep "Model name"
```

### Arquitetura

```bash
lscpu | grep "Architecture"
```

### Núcleos

```bash
lscpu | grep "^Core(s) per socket:"
```

### Threads

```bash
lscpu | grep "^CPU(s):"
```

Essas informações são posteriormente armazenadas em variáveis Bash.

Exemplo:

```text
Modelo: 11th Gen Intel(R) Core(TM) i5-1145G7 @ 2.60GHz
Arquitetura: x86_64
Núcleos: 4
Threads: 8
```

---

# 1.5 — Temperatura da CPU

Além das especificações da CPU, o script coleta a temperatura atual do processador.

Para isso é utilizado:

```bash
sensors
```

O resultado é filtrado para encontrar a temperatura do `Package id 0`:

```bash
sensors 2>/dev/null |
    grep -i "Package id 0" |
    awk '{print $4}' |
    tr -d '+°C' |
    cut -d'.' -f1
```

A temperatura é convertida para um número inteiro.

Exemplo:

```text
Temperatura: 47 °C
```

### Observação

Essa temperatura representa **a temperatura no momento em que o script foi executado**.

Ela ainda não representa a temperatura máxima durante um benchmark.

Posteriormente poderá ser implementado um sistema para acompanhar a temperatura durante testes de desempenho.

---

# 1.6 — Informações da memória RAM

A quantidade total de memória é obtida através de:

```bash
free -g
```

O script utiliza:

```bash
free -g | awk '/Mem:/ {print $2}'
```

para obter a quantidade total de RAM em GB.

Além da quantidade, o script utiliza o `dmidecode` para obter informações sobre os módulos de memória:

```bash
sudo dmidecode --type memory
```

São extraídas:

- Tipo da memória;
- Velocidade configurada.

O tipo é obtido através de:

```bash
grep -i "Type:"
```

enquanto a velocidade utiliza:

```bash
grep -i "Configured Memory Speed:"
```

Exemplo de resultado:

```text
Total: 14 GB
Tipo: DDR4
Velocidade: 3200 MHz
```

A quantidade de RAM retornada pelo sistema pode ser menor que a capacidade física instalada. Isso ocorre porque parte da memória pode estar reservada para hardware, como uma GPU integrada.

---

# 1.7 — Informações da GPU

A GPU é identificada através do comando:

```bash
lspci
```

O script filtra dispositivos relacionados a vídeo:

```bash
lspci | grep -Ei "vga|3d|display"
```

Dessa forma, são identificadas GPUs integradas e dedicadas reconhecidas pelo sistema.

Exemplo:

```text
Intel Corporation TigerLake-LP GT2 [Iris Xe Graphics] (rev 01)
```

Atualmente o projeto armazena apenas o **modelo da GPU**.

Informações adicionais, como memória de vídeo e desempenho em benchmark, poderão ser adicionadas posteriormente.

---

# 1.8 — Informações do armazenamento

O armazenamento é uma das principais categorias para avaliar o notebook.

Primeiramente, o script identifica o primeiro disco interno disponível e ignora
dispositivos USB, evitando analisar o pendrive utilizado para iniciar o Ubuntu
Live:

```bash
lsblk -d -n -o NAME,TYPE,TRAN |
    awk '$2 == "disk" && $3 != "usb" { print $1; exit }'
```

O dispositivo encontrado é utilizado como alvo para as consultas seguintes.

Por exemplo:

```text
/dev/sda
```

ou:

```text
/dev/nvme0n1
```

## Modelo e número de série

O `smartctl` é utilizado para obter informações do dispositivo:

```bash
sudo smartctl -i "$disk_path"
```

O script extrai:

- Modelo;
- Número de série.

Exemplo:

```text
Modelo: SAMSUNG MZVLB256HBHQ-000L7
Serial: S4ELNX2T190178
```

## Capacidade

A capacidade é obtida através do:

```bash
lsblk
```

utilizando:

```bash
lsblk -d -n -o SIZE "$disk_path"
```

Exemplo:

```text
238,5G
```

## Tipo do armazenamento

O script verifica:

```bash
/sys/block/$target_disk/queue/rotational
```

Quando o valor é:

```text
0
```

o dispositivo é considerado:

```text
SSD
```

Quando é:

```text
1
```

é considerado:

```text
HDD
```

---

# 1.9 — Saúde do armazenamento

O `smartctl` também é utilizado para verificar o estado de saúde do dispositivo:

```bash
sudo smartctl -H "$disk_path"
```

O resultado é filtrado para obter o status de saúde.

Exemplo:

```text
PASSED
```

Esse dado será utilizado posteriormente como uma das métricas de conservação do notebook.

Além da saúde geral, o script coleta as horas de funcionamento:

```bash
sudo smartctl -A "$disk_path"
```

e procura pelo atributo:

```text
Power_On_Hours
```

Exemplo:

```text
Power-on hours: 1060
```

Isso permite saber aproximadamente quanto tempo o dispositivo permaneceu em funcionamento ao longo de sua vida útil.

### Limitação atual

O script atualmente analisa apenas o **primeiro disco físico encontrado**.

Caso um notebook possua dois ou mais dispositivos de armazenamento, os demais ainda não são coletados.

Essa funcionalidade poderá ser implementada posteriormente.

---

# 1.10 — Informações da bateria

A bateria é utilizada como uma das principais métricas de conservação do notebook.

O script procura a primeira bateria disponível em:

```bash
/sys/class/power_supply/BAT*
```

Caso uma bateria exista, são coletadas informações relacionadas à sua capacidade e ciclos.
Nesta primeira versão, notebooks com mais de uma bateria têm apenas a primeira
bateria encontrada coletada.

## Ciclos

O número de ciclos é obtido através de:

```bash
cat "$bat_path/cycle_count"
```

Exemplo:

```text
114 ciclos
```

## Capacidade da bateria

O script tenta obter um par compatível de capacidade atual e de projeto. Primeiro
utiliza:

```bash
energy_full
```

Caso o par `energy_*` não esteja disponível, utiliza:

```bash
charge_full
```

Também é obtida a capacidade de projeto:

```text
energy_full_design
```

ou:

```text
charge_full_design
```

A saúde da bateria é então calculada:

```text
Saúde = capacidade atual / capacidade de projeto × 100
```

Por exemplo:

```text
Capacidade de projeto: 50 Wh
Capacidade atual:      43 Wh

Saúde ≈ 86%
```

O JSON final armazena o percentual calculado, as duas capacidades usadas no
cálculo e sua unidade (`uWh` ou `uAh`).

---

# 1.11 — Tratamento de dados ausentes

Como diferentes notebooks podem apresentar informações diferentes, o script possui tratamento para dados que não estejam disponíveis.

Quando uma informação textual não é encontrada, o valor geralmente é definido como:

```text
N/A
```

Quando uma informação numérica não está disponível, o valor é definido como:

```json
null
```

Por exemplo:

```json
{
  "cores": null,
  "threads": null,
  "temperature_celsius": null
}
```

Isso é importante porque `null` representa corretamente a ausência de uma informação, enquanto `0` significaria que o valor realmente é zero.

---

# 1.12 — Geração do JSON

Depois que todas as informações são coletadas e tratadas, o script utiliza o `jq` para criar o arquivo:

```text
result.json
```

A estrutura atual do JSON é dividida em seis grupos principais:

```text
computer
cpu
memory
gpu
storage
battery
```

A estrutura é:

```json
{
  "computer": {},
  "cpu": {},
  "memory": {},
  "gpu": {},
  "storage": {},
  "battery": {}
}
```

## Estrutura `computer`

Contém informações para identificação do notebook:

```json
"computer": {
  "manufacturer": "LENOVO",
  "model": "ThinkPad L14 Gen 2 (20X2SBGJ00)",
  "serial_number": "PE0A71ZL"
}
```

## Estrutura `cpu`

Contém:

```json
"cpu": {
  "model": "11th Gen Intel(R) Core(TM) i5-1145G7 @ 2.60GHz",
  "architecture": "x86_64",
  "cores": 4,
  "threads": 8,
  "temperature_celsius": 47
}
```

## Estrutura `memory`

Contém:

```json
"memory": {
  "total_gb": 14,
  "type": "DDR4",
  "speed_mhz": 3200
}
```

## Estrutura `gpu`

Atualmente contém somente:

```json
"gpu": {
  "model": "Intel Corporation TigerLake-LP GT2 [Iris Xe Graphics] (rev 01)"
}
```

## Estrutura `storage`

Contém:

```json
"storage": {
  "model": "SAMSUNG MZVLB256HBHQ-000L7",
  "serial_number": "S4ELNX2T190178",
  "size": "238,5G",
  "type": "SSD",
  "health_status": "PASSED",
  "power_on_hours": 1060
}
```

## Estrutura `battery`

Contém:

```json
"battery": {
  "health_percentage": 86,
  "cycle_count": 114,
  "full_capacity": 43000,
  "design_capacity": 50000,
  "capacity_unit": "uWh"
}
```

---

# 1.13 — Resultado atual

Depois da execução do script, o resultado é salvo em:

```text
result.json
```

O arquivo representa um "snapshot" do notebook no momento em que a coleta foi realizada.

Exemplo simplificado:

```json
{
  "computer": {
    "manufacturer": "LENOVO",
    "model": "ThinkPad L14 Gen 2 (20X2SBGJ00)",
    "serial_number": "PE0A71ZL"
  },
  "cpu": {
    "model": "11th Gen Intel(R) Core(TM) i5-1145G7 @ 2.60GHz",
    "architecture": "x86_64",
    "cores": 4,
    "threads": 8,
    "temperature_celsius": 47
  },
  "memory": {
    "total_gb": 14,
    "type": "DDR4",
    "speed_mhz": 3200
  },
  "gpu": {
    "model": "Intel Corporation TigerLake-LP GT2 [Iris Xe Graphics] (rev 01)"
  },
  "storage": {
    "model": "SAMSUNG MZVLB256HBHQ-000L7",
    "serial_number": "S4ELNX2T190178",
    "size": "238,5G",
    "type": "SSD",
    "health_status": "PASSED",
    "power_on_hours": 1060
  },
  "battery": {
    "health_percentage": 86,
    "cycle_count": 114,
    "full_capacity": 43000,
    "design_capacity": 50000,
    "capacity_unit": "uWh"
  }
}
```

---

# 1.14 — Estado atual e próximos passos

A primeira versão do coletor já é capaz de obter informações de:

- Identificação do computador;
- CPU;
- Temperatura atual da CPU;
- Memória RAM;
- GPU;
- Armazenamento;
- Saúde do armazenamento;
- Horas de funcionamento do armazenamento;
- Saúde da bateria;
- Ciclos da bateria.

Neste momento, o script **apenas gera o arquivo `result.json`**.

O `curl` já está instalado e preparado para ser utilizado, mas o envio para a API ainda será implementado em uma etapa posterior.

Também ainda não foram implementados os **benchmarks de desempenho**.

Portanto, o fluxo atual é:

```text
Ubuntu Live
     ↓
collector.sh
     ↓
Coleta de informações
     ↓
Tratamento
     ↓
jq
     ↓
result.json
```

O fluxo planejado para a próxima etapa será:

```text
Ubuntu Live
     ↓
collector.sh
     ↓
Coleta
     ↓
JSON
     ↓
curl
     ↓
Spring Boot API
```

Posteriormente serão adicionados os testes de desempenho, permitindo que o sistema não avalie apenas as especificações e o estado de conservação, mas também o desempenho real de cada notebook.
