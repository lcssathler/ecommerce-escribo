# E-commerce Escribo

E-commerce desenvolvida com **Java 21 + Spring Boot 3.5**, integrada ao **Supabase** para armazenamento de dados, autenticação e automação de processos através de **Edge Functions** e **triggers SQL**.

O sistema permite gerenciar clientes, produtos e pedidos, realizando cálculos automáticos de subtotal e total, além de enviar e-mails de confirmação e gerar relatórios CSV de pedidos.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5**
- **WebFlux (WebClient)**
- **PostgreSQL (via Supabase)**
- **Supabase Edge Functions (automação e notificações)**
- **Triggers e Functions SQL**
- **Views para consultas otimizadas**

---

## 🧩 Modelagem do Banco de Dados

O banco de dados foi modelado no **Supabase (PostgreSQL)** com as seguintes tabelas principais:

### **1. client**
Contém os dados dos clientes registrados, vinculados a um usuário autenticado do Supabase (`auth.users`).

| Coluna     | Tipo      | Descrição |
|-------------|-----------|------------|
| id          | bigint    | Identificador único |
| name        | varchar   | Nome do cliente |
| email       | varchar   | Email do cliente |
| user_id     | uuid      | ID do usuário Supabase |
| created_at  | timestamp | Data de criação |

---

### **2. product**
Representa os produtos disponíveis no estoque.

| Coluna     | Tipo      | Descrição |
|-------------|-----------|------------|
| id          | bigint    | Identificador único |
| name        | varchar   | Nome do produto |
| price       | numeric   | Valor unitário |
| quantity    | integer   | Quantidade em estoque |
| created_at  | timestamp | Data de criação |

---

### **3. orders**
Tabela que representa os pedidos de cada cliente.

| Coluna     | Tipo      | Descrição |
|-------------|-----------|------------|
| id          | bigint    | Identificador único |
| client_id   | bigint    | FK para `client` |
| total       | numeric   | Valor total do pedido |
| status      | enum      | `PENDING`, `APPROVED`, `CANCELLED` |
| created_at  | timestamp | Data de criação |

---

### **4. order_product**
Tabela intermediária que representa a **relação entre pedidos e produtos**.

| Coluna       | Tipo      | Descrição |
|---------------|-----------|------------|
| id            | bigint    | Identificador único |
| orders_id     | bigint    | FK para `orders` |
| product_id    | bigint    | FK para `product` |
| unity_price   | numeric   | Valor unitário no momento da compra |
| quantity      | integer   | Quantidade comprada |
| subtotal      | numeric   | `quantity * unity_price` |
| created_at    | timestamp | Data de criação |

> 💡 Essa tabela é o elo entre `orders` e `product`.  
> Cada linha indica quantos produtos foram comprados em um pedido e a que preço.

---

## Cálculos Automáticos (Functions e Triggers)

O banco conta com **funções automáticas em PostgreSQL** para:

- Calcular automaticamente o **subtotal** em `order_product` ao inserir ou atualizar registros;
- Atualizar o **total do pedido** na tabela `orders`;
- Gerenciar automaticamente o **estoque** (`product.quantity`) quando um pedido é criado, cancelado ou excluído.

Essas automações são implementadas via **triggers**, garantindo consistência e eliminando a necessidade de cálculos manuais no backend.

---

## Views Criadas

Para otimizar consultas e facilitar integrações, foram criadas views no Supabase, como:

- **view_order_details** → Lista todos os pedidos com seus produtos e valores agregados;  
- **view_client_orders** → Lista os pedidos agrupados por cliente;  
- **view_stock_summary** → Mostra o estoque atual e o total de produtos vendidos.

Essas views permitem buscas rápidas sem necessidade de múltiplos `JOINs` no backend.

---

## 🌐 API REST — Backend (Spring Boot)

O backend expõe endpoints RESTful que se comunicam com o banco Supabase via **Spring WebClient**.  
As chamadas seguem o padrão:

```java
webClient.get()
    .uri(supabaseUrl + "/rest/v1/client?select=*")
    .header("apikey", supabaseAnonKey)
    .header("Authorization", "Bearer " + token)
    .retrieve()
    .bodyToMono(ClientDTO[].class)
    .block();
