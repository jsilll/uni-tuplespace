Distributed Ledger
================ 

Este documento descreve o projeto da cadeira de Sistemas Distribuídos 2023/2024.

1 Introdução
------------

O objetivo do projeto de Sistemas Distribuídos (SD) é desenvolver o
sistema **TupleSpace**, um serviço que implementa um *espaço de tuplos* distribuído. 

O serviço permite a um ou mais utilizadores colocarem tuplos no espaço partilhado, lerem os tuplos existentes, assim como retirarem tuplos do espaço. Um tuplo é um conjunto ordenado de campos *<campo_1, campo_2, ..., campo_n>*. Por exemplo, *<vaga, sd, turno1>*. 

No espaço de tuplos podem co-existir várias instâncias de um mesmo tuplo (por exemplo, podem existir múltiplos tuplos *<vaga, sd, turno1>*, indicando a existência de várias vagas. Quando se procuram tuplos no espaço de tuplos (para ler ou retirar), é possível indicar quais os tuplos que se procuram usando o caracter especial "\*", para permitir o emparelhamento com múltiplos valores. Por exemplo, é possível procurar por *<vaga, sd, \*>*, que tanto emparelha com  *<vaga, sd, turno1>* como com  *<vaga, sd, turno2>*. Mais informação sobre os espaços de tuplos distribuídos, assim como a descriçao de um sistema que concretiza esta abstração pode ser encontrada na bibilografia da cadeira e no seguinte artigo:

A. Xu and B. Liskov. A design for a fault-tolerant, distributed implementation of linda. In 1989 The Nineteenth International Symposium on Fault-Tolerant Computing. Digest of Papers(FTCS), pages 199–206.


As operações disponíveis para o utilizador são as seguintes (usamos a nomenclatura em Inglês, exactamente como é usada na bibliografia da cadeira):  *add*, *read* e *take*.

* O operador *add* acrescenta um tuplo ao espaço partilhado.

* O operador *read* aceita a descrição do tuplo (recorrendo ao caracter "*") e retorna *um* tuplo que emparelhe com a descrição caso exita, ou *null* caso não exista nenhum tuplo que emarelehe com a descrição. O tuplo *não* é retirado do espaço de tuplos.

* O operador *take* aceita a descrição do tuplo (recorrendo ao caracter "*") e retorna *um* tuplo que emparelhe com a descrição. Esta operação bloqueia o cliente até que exista um tuplo que satisfaça a descrição. O tuplo *é* retirado do espaço de tuplos.

Um utilizador acede ao serviço **TupleSpace** através de um processo cliente, que interage 
com um ou mais servidores que oferecem o serviço, através de chamadas a procedimentos remotos.
Há também um cliente de administração, que fornece operações especiais (que descreveremos adiante).
O sistema será concretizado através de um conjunto de serviços gRPC implementados na plataforma Java.

O projeto está estruturado em três fases, descritas de seguida.

2 Estado dos servidores
------------------------

O servidor (ou servidores, conforme a fase) mantém, pelo menos, o estado seguinte: 

* Uma lista dos tuplos que existem no espaço de tuplos.

3 Arquitetura do sistema
------------------------

O sistema usa uma arquitetura cliente-servidor. O serviço é fornecido por um ou mais servidores que podem ser
contactados por processos cliente, através de chamadas a procedimentos remotos. 

Em cada fase do projeto será explorada uma variante desta arquitetura básica, tal como se descreve abaixo.


3.1 Fase 1
-------------------

Nesta fase o serviço é prestado por um único servidor, que aceita pedidos num endereço/porto fixo que é conhecido de antemão por todos os clientes.

3.2 Fase 2
-------------------

Na segunda fase, o serviço é fornecido por dois servidores: um primário e um secundário. Uma operação que altere o estado do sistema (que passaremos a designar como  *operação de escrita*) só pode ser realizada no primário, que
propaga a mesma para o secundário antes de responder ao cliente. 

Operações que não alterem o estado do sistema (que passaremos a designar como *operações de leitura*), podem ser invocadas e executadas em qualquer um dos servidores.

Para além disso, nesta fase nem os clientes nem os servidores sabem, quando são lançados, quais os endereços dos (outros) servidores. Para permitir que o endereço dos servidores seja descoberto dinamicamente, a solução deverá recorrer a um servidor de nomes **(a ser desenvolvido nas aulas teórico-práticas)**.


3.3 Fase 3
-------------------

Na última fase, o serviço é fornecido por dois servidores mas todas as operações passam a poder ser feitas em todos os servidores. Os clientes ligam-se a um dos servidores, escolhido de forma aleatória.


4 Interfaces do serviço
------------------------

Cada servidor exporta múltiplas interfaces, cada uma pensada para diferentes tipos de clientes(clientes e administradores). Para além dessas, os servidores exportam uma terceira interface pensada para ser invocada por outros servidores (no caso em que os servidores estão replicados, e necessitam de comunicar entre si).

4.1 Interface do utilizador
-------------------

O utilizador pode invocar as seguintes funções:

- `add` 
- `read` 
- `take` 

4.2 Interface do administrador
-------------------

- `activate` -- coloca o servidor em modo **ATIVO** (este é o comportamento por omissão), em que responde a todos os pedidos
- `deactivate` -- coloca o servidor em modo **INATIVO**. Neste modo o servidor responde com o erro "UNAVAILABLE" a todos os pedidos dos utilizadores
- `getTupleSpacesState` -- apresenta o conteúdo do espaço de tuplos local

4.3 Interface entre servidores (só fases 2 e 3)
-------------------

A interface entre servidores será definida pelos alunos.

5 Servidor de nomes (só fases 2 e 3)
------------------------

O servidor de nomes permite aos servidores registarem o seu endereço para ser conhecido por outros que estejam presentes no sistema.

Um servidor, quando se regista, indica o nome do serviço (neste caso *TupleSpace*), o seu endereço e um qualificador, que pode assumir os valores 'A', 'B', etc. Na 2ª fase, o servidor primário é apenas o 'A'.

Este servidor está à escuta no porto **5001** e assume-se que nunca falha.

Os clientes podem obter o endereço dos servidores, fornecendo o nome do serviço e o qualificador.

Nas fases 2 e 3, cada um dos servidores também pode usar o servidor de nomes para ficar a saber o endereço do outro servidor.

6 Processos
------------------------


6.1 Servidores
--------------

O servidor (ou servidores nas fases 2 e 3) devem ser lançados a partir da pasta `Server`, recebendo como argumentos o porto e o seu qualificador ('A', 'B', etc.). Na fase 1, o qualificador passado é ignorado.

Por exemplo, um servidor primário pode ser lançado da seguinte forma a partir da pasta `Server` (**$** representa a *shell* do sistema operativo):

`$ mvn exec:java -Dexec.args="2001 A"`

Um servidor secundário pode ser lançado da seguinte forma:

`$ mvn exec:java -Dexec.args="2002 B"`


6.2 Servidor de nomes (só fases 2 e 3)
-------------

O servidor de nomes deve ser lançado sem argumentos e ficará à escuta no porto `5001`, podendo ser lançado a partir da pasta `NamingServer` da seguinte forma:

`$ mvn exec:java`


6.3 Clientes
-------------

Ambos os tipos de processo cliente (utilizador e administrador) recebem comandos a partir da consola. Todos os processos cliente deverão mostrar o símbolo *>* sempre que se encontrarem à espera que um comando seja introduzido.

Para todos os comandos, caso não ocorra nenhum erro, os processos cliente devem imprimir "OK" seguido da mensagem de resposta, tal como gerada pelo método toString() da classe gerada pelo compilador `protoc`, conforme ilustrado nos exemplos abaixo. 

No caso em que um comando origina algum erro do lado do servidor, esse erro deve ser transmitido ao cliente usando os mecanismos do rRPC para tratamento de erros (no caso do Java, encapsulados em exceções). Nessas situações, quando o cliente recebe uma exceção após uma invocação remota, este deve simplesmente imprimir uma mensagem que descreva o erro correspondente.

Na fase 1, os programas de ambos os tipos de clientes recebem como argumentos o nome da máquina e porto onde o servidor do TupleSpace pode ser encontrado. Por exemplo:

`$ mvn exec:java -Dexec.args="localhost 2001"`

A partir da fase 2, os programas cliente deixam de ter quaisquer argumentos.


6.4 Opção de *debug*

Todos os processos devem poder ser lançados com uma opção "-debug". Se esta opção for seleccionada, o processo deve imprimir para o "stderr" mensagens que descrevam as ações que executa. O formato destas mensagens é livre, mas deve ajudar a depurar o código. Deve também ser pensado para ajudar a perceber o fluxo das execuções durante a discussão final.


7 Tecnologia
------------

Todos os componentes do projeto têm de ser implementados na linguagem de
programação [Java](https://docs.oracle.com/javase/specs/).

A ferramenta de construção a usar, obrigatoriamente, é o [Maven](https://maven.apache.org/).

### Invocações remotas

A invocação remota de serviços deve ser suportada por serviços [gRPC](https://grpc.io/).

Os serviços implementados devem obedecer aos *protocol buffers* fornecidos no código base disponível no repositório github do projeto.


### Persistência

Não se exige nem será valorizado o armazenamento persistente do estado dos servidores.
Caso um servidor falhe, assume-se que este retoma a sua execução com o estado que tinha aquando do momento da sua falha. 
Isto pode ser simulado usando as operações *activate* e *deactivate* a partir do cliente administrador.


### Validações

Os argumentos das operações devem ser validados obrigatoriamente e de forma estrita pelo servidor.

Os clientes podem optar por também validar, de modo a evitar pedidos desnecessários para o servidor, mas podem optar por
uma versão mais simples da validação.

# 8 Modelo de Faltas

Deve assumir-se um sistema síncrono _(em particular, assume-se que as mensagens enviadas irão eventualmente ser entregues, dentro de um intervalo de tempo limitado)_, em que as falhas são silenciosas. Não ocorrem falhas bizantinas. 

Se durante a execução surgirem faltas, ou seja, acontecimentos inesperados, o programa deve apanhar a exceção, imprimir
informação sucinta e pode parar de executar.

Se for um servidor, o programa deve responder ao cliente com um código de erro adequado.

Se for um dos clientes, pode decidir parar com o erro recebido ou fazer novas tentativas de pedido.

Fica fora do âmbito do projeto resolver os problemas relacionados com a segurança (e.g., autenticação dos 
utilizadores ou administradores, confidencialidade ou integridade das mensagens).
Também deve ser assumido que os utilizadores são bem-comportados. Em particular, que um utilizador 
não usa dois processos cliente em simultâneo para, a partir destes, executar operações concorrentes envolvendo 
a sua conta.



# 9 Resumo
------------

Em resumo, é necessário implementar:

- Na fase 1 (1ª entrega):
  - servidor único;
  - clientes;
 

- Na fase 2 (2ª entrega):
  - servidores primário e secundário (extensão do servidor da fase 1);
  - clientes, agora a consultar o servidor de nomes.

- Na fase 3 (3ª entrega):
  - múltiplos servidores activos;
  - clientes, a consultar o servidor de nomes.



10 Avaliação
------------

10.1 Fotos
---------

Cada membro da equipa tem que atualizar o Fénix com uma foto, com qualidade, tirada nos últimos 2 anos, para facilitar a
identificação e comunicação.

10.2 Identificador de grupo
--------------------------

O identificador do grupo tem o formato `GXX`, onde `G` representa o campus e `XX` representa o número do grupo de SD
atribuído pelo Fénix. Por exemplo, o grupo A22 corresponde ao grupo 22 sediado no campus Alameda; já o grupo T07
corresponde ao grupo 7 sediado no Taguspark.

O grupo deve identificar-se no documento `README.md` na pasta raiz do projeto.

Em todos os ficheiros de configuração `pom.xml` e de código-fonte, devem substituir `GXX` pelo identificador de grupo.

Esta alteração é importante para a gestão de dependências, para garantir que os programas de cada grupo utilizam sempre
os módulos desenvolvidos pelo próprio grupo.

10.3 Colaboração
---------------

O [Git](https://git-scm.com/doc) é um sistema de controlo de versões do código fonte que é uma grande ajuda para o
trabalho em equipa.

Toda a partilha de código para trabalho deve ser feita através do [GitHub](https://github.com).

O repositório de cada grupo está disponível em: https://github.com/tecnico-distsys/GXX-DistLedger/ (substituir `GXX` pelo
identificador de grupo).

A atualização do repositório deve ser feita com regularidade, correspondendo à distribuição de trabalho entre os membros
da equipa e às várias etapas de desenvolvimento.

Cada elemento do grupo deve atualizar o repositório do seu grupo à medida que vai concluindo as várias tarefas que lhe
foram atribuídas.

10.4 Entregas
------------

As entregas do projeto serão feitas também através do repositório GitHub.

A cada parte do projeto a entregar estará associada uma [*tag*](https://git-scm.com/book/en/v2/Git-Basics-Tagging).

As *tags* associadas a cada entrega são `SD_P1`, `SD_P2` e `SD_P3`, respetivamente.
Cada grupo tem que marcar o código que representa cada entrega a realizar com uma *tag* específica antes
da hora limite de entrega.

10.5 Valorização
---------------


As datas limites de entrega estão definidas no site dos laboratórios: (https://tecnico-distsys.github.io)

### Qualidade do código

A avaliação da qualidade engloba os seguintes aspetos:

- Configuração correta (POMs);

- Código legível (incluindo comentários relevantes);

- [Tratamento de exceções adequado](https://tecnico-distsys.github.io/04-rpc-error-test/index.html);

- [Sincronização correta](https://tecnico-distsys.github.io/02-tools-sockets/java-synch/index.html);

- Separação das classes geradas pelo protoc/gRPC das classes de domínio mantidas no servidor.

10.6 Instalação e demonstração
-----------------------------

As instruções de instalação e configuração de todo o sistema, de modo a que este possa ser colocado em funcionamento,
devem ser colocadas no documento `README.md`.

Este documento tem de estar localizado na raiz do projeto e tem que ser escrito em formato *[
MarkDown](https://guides.github.com/features/mastering-markdown/)*.

Cada grupo deve preparar também um mini relatório, que não deve exceder as **500** palavras, explicando a sua solução para a fase 3,
e que não deve repetir informação que já esteja no enunciado. Este documento deve estar na raiz do projeto e deve ter o nome `REPORT.md`

10.7 Discussão
-------------

As notas das várias partes são indicativas e sujeitas a confirmação na discussão final, na qual todo o trabalho
desenvolvido durante o semestre será tido em conta.

As notas a atribuir serão individuais, por isso é importante que a divisão de tarefas ao longo do trabalho seja
equilibrada pelos membros do grupo.

Todas as discussões e revisões de nota do trabalho devem contar com a participação obrigatória de todos os membros do
grupo.

10.8 Atualizações
----------------

Para acompanhar as novidades sobre o projeto, consultar regularmente
a [página Web dos laboratórios](https://tecnico-distsys.github.io).

Caso venham a surgir correções ou clarificações neste documento, podem ser consultadas no histórico (_History_).

10.9 Estrutura do projeto
-------------------------

- [contract](contract/) - contract definition
- [client](client/) - implementation of client
- [server](server/) - implementation of server

See the README for each directory for further details.
Start at proto, then go to server, and finally go to the client.

**Bom trabalho!**
