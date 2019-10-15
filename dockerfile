#obtem image pronta de Clojure
FROM clojure 

#copia todos arquivo do diretorio raiz e mover para pasta  /usr/src/app
COPY . /usr/src/app

#pasta que roda a aplicacao
WORKDIR /usr/src/app

#comando linux 
CMD ["lein", "run"]