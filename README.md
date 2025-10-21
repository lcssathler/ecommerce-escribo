# E-commerce com Supabase e Spring Boot

Teste técnico Supabase + AI

---

## Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.4.4**
- **JPA / Hibernate**
- **Supabase (PostgreSQL + Auth + Edge Functions)**
- **Postman**
- **Resend API**
- **Deno + Supabase Edge Functions**

---

## Estrutura do Banco de Dados

O banco de dados é hospedado no **Supabase**, que utiliza **PostgreSQL**.  
As tabelas principais são:

### **Tabelas**

| Tabela | Descrição |
|--------|------------|
| **product** | Contém as informações dos produtos (nome, preço, estoque, descrição). |
| **order** | Representa um pedido realizado por um usuário. |
| **order_product** | Tabela intermediária que representa o relacionamento N:N entre `order` e `product`. |
| **user** | Usuários autenticados no Supabase (armazenados pelo módulo de Auth). |

### **Relacionamentos**

- Um **Order** pode conter **vários Products**.  
- Um **Product** pode estar em **vários Orders**.  
- Essa relação é implementada pela tabela intermediária **OrderProduct**, que contém:
  - `order_id` (FK para `order`)
  - `product_id` (FK para `product`)
  - `quantity` e `total_price`

---

## ⚙️ Backend com Spring Boot

O backend foi desenvolvido em **Spring Boot**, utilizando o **JPA** para mapear as entidades do banco e interagir com o Supabase. 

### **Fluxo de uma Requisição REST**

1. O **cliente** (Postman, frontend, etc.) envia uma requisição HTTP para o backend Java.  
2. O **Controller** recebe a requisição e valida os headers (Authorization e API Key).  
3. O **Service** utiliza o **JPA** para persistir ou consultar dados no Supabase.  
4. O **Repository** interage com o banco via **Supabase JDBC** (PostgreSQL).  
5. Em alguns casos, o backend também faz chamadas diretas à **API REST do Supabase** usando o token do usuário autenticado.

---

## Autenticação

O fluxo de autenticação é totalmente integrado ao **Supabase Auth**.

1. O usuário é criado e autenticado diretamente no **Supabase**, gerando um **UUID único**.  
2. Com esse UUID, o backend faz uma **requisição POST** para a **API de autenticação do Supabase** (`/auth/v1/token`) para obter um **access token (JWT)**.  
3. Esse token é armazenado temporariamente no backend e usado para autenticar requisições futuras. Somente tokens validados podem fazer as requisições.

---

## 🧾 Uso do Token de Acesso

Para enviar requisições REST diretamente à API do Supabase, é necessário incluir no **header**:

```http
Authorization: Bearer <ACCESS_TOKEN>
apikey: <SUPABASE_ANON_KEY>
Content-Type: application/json
```
Esses headers são necessários tanto no Postman quanto nas chamadas internas do backend ao Supabase.

Exemplo de requisição via Postman para inserir um produto:

```
POST https://<your-project>.supabase.co/rest/v1/product
Headers:
  Authorization: Bearer eyJhbGciOi...
  apikey: SUPABASE_ANON_KEY
Body:
  {
    "name": "Notebook Dell",
    "price": 4999.90,
    "stock": 5
  }
```

## Modificações no Backend

O backend foi adaptado para:

1. Receber requisições REST com os headers de autenticação (Authorization e apikey);
2. Encaminhar essas requisições para a API REST do Supabase, preservando os headers originais;
3. Interagir com o banco via JPA para consultas e operações locais, mantendo a consistência dos dados.

   
## Row Level Security (RLS)

As políticas RLS garantem que cada usuário veja apenas seus próprios dados:

```sql
create policy "Client can view your own profile"
on public.client
for select
to authenticated
using (auth.uid() = user_id);

create policy "Client can insert your own profile"
on public.client
for insert
to authenticated
with check (auth.uid() = user_id);
```
<img width="1087" height="250" alt="image" src="https://github.com/user-attachments/assets/1222ad4a-5fbd-46ad-bb30-7105bf23c650" />


## Funções e Triggers

O banco possui funções e triggers criadas para automatizar processos:

- Atualizar totais automaticamente quando um item é inserido na tabela order_product;
- Recalcular o valor total do pedido na tabela order;
- Devolver produtos ao estoque quando um item é removido;
- Auditar ações e manter integridade entre as tabelas.
<img width="1115" height="455" alt="image" src="https://github.com/user-attachments/assets/f191346d-31f5-4bca-912f-2cf7eecb5b90" />

## Exemplo de uma função:
<img width="447" height="370" alt="image" src="https://github.com/user-attachments/assets/3eb82960-7372-4ca1-b9ce-33e9f5c59aff" />

## Views Criadas

| Visão | Descrição |
|--------|------------|
| **view_order_details** | Mostra detalhes completos de cada pedido, com cliente, produtos, subtotal e total calculado.. |
| **view_client_summary** | Mostra um resumo dos clientes com total gasto e número de pedidos. |
| **view_product_stock** | Mostra os produtos com a quantidade em estoque e quantos foram vendidos. |
| **view_order_summary** | Mostra os totais de cada pedido, ideal pra dashboards. |

## Edge Functions — Envio de E-mail Automático

Foi criada uma Edge Function no Supabase para enviar e-mails de confirmação de compra.

Fluxo:

- Um novo registro é inserido na tabela order_product.
- O trigger do banco chama a Edge Function via pg_net.http_post.
- A Edge Function envia um e-mail para o cliente com os detalhes da compra, utilizando a API do Resend.

Exemplo simplificado da função:

```js
const RESEND_API_KEY = Deno.env.get('RESEND_API_KEY')

const handler = async (_request: Request): Promise<Response> => {
  const res = await fetch('https://api.resend.com/emails', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${RESEND_API_KEY}`,
    },
    body: JSON.stringify({
      from: 'onboarding@resend.dev',
      to: 'delivered@resend.dev',
      subject: 'hello world',
      html: '<strong>it works!</strong>',
    }),
  })

  const data = await res.json()

  return new Response(JSON.stringify(data), {
    status: 200,
    headers: {
      'Content-Type': 'application/json',
    },
  })
}

Deno.serve(handler)
```

## Testando com Postman

Exemplo de requisição para o backend Java:

```http
POST http://localhost:8080/orders
Headers:
  Authorization: Bearer <ACCESS_TOKEN>
  apikey: <SUPABASE_ANON_KEY>
  Content-Type: application/json
Body:
  {
    "userId": "UUID_DO_USUARIO",
    "products": [
      { "productId": 1, "quantity": 2 },
      { "productId": 3, "quantity": 1 }
    ]
  }
```
