# A respeito do script collector.sh

- **Apenas o primeiro disco é analisado.**
- **A bateria é procurada especificamente como `BAT0`.**
- **A temperatura depende dos sensores disponíveis.**
- **Múltiplas GPUs podem ser retornadas juntas.**
- **Algumas informações SMART podem não existir em determinados SSDs.**
- **A RAM é arredondada pelo `free -g`.**

|Área|Estado|
|---|---|
|Identificação|🟢 Muito boa|
|CPU|🟢 Boa|
|Temperatura|🟡 Funciona, mas depende do sensor|
|RAM|🟢 Boa|
|GPU|🟡 Funciona, mas múltiplas GPUs podem ser problemáticas|
|Storage|🟡 Boa para 1 disco|
|SMART|🟢 Boa, com limitações naturais|
|Bateria|🟢 Boa|
|Tratamento de dados ausentes|🟢 Muito bom|
|JSON|🟢 Muito bom|
|Complexidade|🟢 Adequada ao projeto|

