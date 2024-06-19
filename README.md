# Simple transactionsystem

A simple microservice ecosystem that behaves like a very simple transaction system.

## API Reference

#### Create an account

```http
  POST /accounts
```

| Parameter | Type     | 
| :-------- | :------- |
| `name` | `string` | 
| `email` | `string` |
| `openingAmount` | `number` | 

#### Get account information

```http
  GET /accounts/${id}
```

#### Transfer
```http
  POST /transactions/transfer
```
| Parameter | Type     | 
| :-------- | :------- |
| `senderAccountId` | `number` | 
| `recipientAccountId` | `number` |
| `amount` | `number` | 
