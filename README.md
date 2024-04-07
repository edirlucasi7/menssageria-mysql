# menssageria-mysql

### Configurações da aplicação

* Java JDK 17
* Mysql: 8.0.36

# Detalhes

- Aplicação que simula o consumo de eventos de uma fila, porém, utilizando um banco de dados relacional. Sem broker (kafka, RabbitMQ), sem SQS, SNS. Apenas
fazendo uso do bom e velho banco de dados relacional. https://dev.mysql.com/blog-archive/mysql-8-0-1-using-skip-locked-and-nowait-to-handle-hot-rows/

- Usei uma estratégia que alguns bancos de dados como PostegreSQL, Oracle e Mysql oferecem suporte, chamada de FOR UPDATE E SKIP LOCKED. Basicamente, é possível
informar as duas cláusulas durante a execução de um filtro no banco de dados:

![image](https://github.com/edirlucasi7/menssageria-mysql/assets/28410756/50ed806f-7d25-44e9-96fb-0e1882dfc640)

De modo geral, qualquer consulta com `FOR UPDATE` trava explicitamente as linhas afetadas pela consulta durante uma transação. Isso impede que outras transações modifiquem essas linhas até que a transação que as bloqueou seja concluída.

Enquanto que o `SKIP LOCKED` permite que uma consulta ignore linhas que estão bloqueadas por outras transações.

É possível testar da seguinte forma: cadastre 20 ou mais ordens através do endpoint: `http://localhost:8089/api/order/create`, com seguinte corpo:

```
{
	"name": "Carregador",
	"amount": 70
}
```

Em seguida, chame o endpoint `http://localhost:8089/api/order/process` e verifique as ordens com `status` atualizado.

![image](https://github.com/edirlucasi7/menssageria-mysql/assets/28410756/6dc7bc9c-2e7d-4b62-b01a-452dee312245)

Em resumo, uma das transações obtém o lock da linha em questão e continua em busca da  próxima linha que ainda não obteve lock. Assim, cada transação retorna 10 resultados das respectivas linhas que não estavam com skip locked fornecido por outra transação.
