# Nubank - Bank operationg test

This development was proposed by Nubank as a challenge to code a process of bank operations.

During the interview I have noticed that the Clojure is the language more used by the company. So I decided to learn and implement the requirements requested on this language.

It was my first time with functional programming. I hope the code is reasonable for your quality level. The mission is now done. :)


## Build / Deploy

1. Step 1: Unzip file to directory

Passo 0:  Download da imagem $docker pull clojure
Passo 1:  Descompacte o arquivo .zip  
Passo 2:  No diretorio onde descompactou o arquivo zip, rode o comando $docker build -t Nubank_test .  
Passo 3:  Em seguinda execute $docker run -it Nubank_test /bin/bash  
Passo 4:  Estando logado dentro do container execute o seguinte comando:   
          $lein run  Após este passo insira o json necessário.

## Usage

`lein run`

Next, please input on the prompt the transactions test in json format.

```
$ lein run
{ "account": { "activeCard": true, "availableLimit": 100 } }
{ "transaction": { "merchant": "Burger King", "amount": 20, "time": "2019-02-13T10:00:00.000Z" } }
{ "transaction": { "merchant": "Habbib's", "amount": 90, "time": "2019-02-13T11:00:00.000Z" } }

```

The answer will be presented immediately after the each line of input, like a stream data service.

## Test

I have implemented plus violations code as "chronology-error" and "invalid-input". First is assigned for any new transaction with operation date before a last valid transaction.  Second is assigned for any input out of the pattern.

I have made 12 cases tests for a totally 42 verifications. Some tests as unit test to valid operations, some Boundary tests for valid the transaction limit  and some integrated tests to valid a sequence of operations.

## External Libraries

I have used only open source libraries in this development. My featured is library to work with dates and with json.
