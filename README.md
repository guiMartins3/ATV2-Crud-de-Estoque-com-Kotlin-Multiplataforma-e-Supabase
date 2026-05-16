# Estocadão API 📦

API REST para controle de estoque, desenvolvida com **Kotlin + Ktor**, conectada ao **Supabase (PostgreSQL)**.

---

## Pré-requisitos

- JDK 17+
- Gradle 8+ (ou use o wrapper `./gradlew`)
- Conta no [Supabase](https://supabase.com) (gratuita)

---

## 1. Configuração do Supabase (passo a passo)

### 1.1 Criar o projeto

1. Acesse [supabase.com](https://supabase.com) e faça login
2. Clique em **"New project"**
3. Preencha:
   - **Name:** `estocadao`
   - **Database Password:** escolha uma senha forte (guarde!)
   - **Region:** escolha a mais próxima (ex: South America - São Paulo)
4. Clique em **"Create new project"** e aguarde ~2 minutos

### 1.2 Criar as tabelas via SQL Editor

1. No menu lateral, clique em **"SQL Editor"**
2. Clique em **"New query"**
3. Cole todo o conteúdo do arquivo `sql/schema.sql`
4. Clique em **"Run"** (▶️)
5. Você verá `Success. No rows returned` — isso é normal!

### 1.3 Pegar as credenciais

1. No menu lateral, clique em **"Project Settings"** (ícone de engrenagem)
2. Clique em **"API"**
3. Copie:
   - **Project URL** → vai para `SUPABASE_URL`
   - **Project API Keys > anon public** → vai para `SUPABASE_KEY`

> ⚠️ **NUNCA use a `service_role` key** no código. Use sempre a `anon` key.

---

## 2. Configuração do projeto

### 2.1 Clonar e configurar variáveis de ambiente

```bash
git clone https://github.com/seu-usuario/estocadao.git
cd estocadao

# Copie o exemplo e preencha com suas credenciais
cp .env.example .env
```

Edite o arquivo `.env`:

```env
SUPABASE_URL=https://xxxxxxxxxxx.supabase.co
SUPABASE_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
PORT=8080
```

### 2.2 Executar a aplicação

```bash
./gradlew run
```

A API estará disponível em `http://localhost:8080`

### 2.3 Verificar saúde da API

```bash
curl http://localhost:8080/health
# {"status":"ok","service":"Estocadão API"}
```

---

## 3. Endpoints

### Produtos — `/products`

| Método | Rota             | Descrição                    |
|--------|------------------|------------------------------|
| GET    | /products        | Lista todos os produtos      |
| GET    | /products/{id}   | Busca produto por ID         |
| POST   | /products        | Cadastra novo produto        |
| PUT    | /products/{id}   | Atualiza dados do produto    |
| DELETE | /products/{id}   | Remove produto               |

**Exemplo — Criar produto:**
```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Caneta Azul","sku":"CAN-001","category":"Papelaria"}'
```

### Estoque — `/stock`

| Método | Rota             | Descrição                       |
|--------|------------------|---------------------------------|
| GET    | /stock           | Lista todos os itens de estoque |
| GET    | /stock/{id}      | Busca item por ID               |
| POST   | /stock           | Adiciona item ao estoque        |
| PUT    | /stock/{id}      | Atualiza item do estoque        |
| DELETE | /stock/{id}      | Remove item do estoque          |
| GET    | /stock/summary   | Total de cada produto em estoque|

**Exemplo — Adicionar ao estoque:**
```bash
curl -X POST http://localhost:8080/stock \
  -H "Content-Type: application/json" \
  -d '{"product_id":"uuid-do-produto","quantity":100,"unit_price":2.50,"location":"A1"}'
```

**Exemplo — Resumo do estoque:**
```bash
curl http://localhost:8080/stock/summary
# [{"product_id":"...","product_name":"Caneta Azul","total_quantity":100}]
```

---

## 4. Variáveis de ambiente

| Variável       | Obrigatória | Descrição                                      |
|----------------|-------------|------------------------------------------------|
| `SUPABASE_URL` | ✅ Sim      | URL do projeto Supabase                        |
| `SUPABASE_KEY` | ✅ Sim      | Chave `anon public` do Supabase                |
| `PORT`         | ❌ Não      | Porta do servidor (padrão: 8080)               |

---

## 5. Estrutura do projeto

```
estocadao/
├── sql/
│   └── schema.sql              # SQL para criar tabelas no Supabase
├── src/main/kotlin/com/estocadao/
│   ├── Application.kt          # Ponto de entrada
│   ├── SupabaseClient.kt       # Configuração do cliente HTTP
│   ├── models/
│   │   └── Models.kt           # Data classes (Product, StockItem, etc.)
│   ├── services/
│   │   ├── ProductService.kt   # Lógica de negócio - Produtos
│   │   └── StockService.kt     # Lógica de negócio - Estoque
│   ├── routes/
│   │   ├── ProductRoutes.kt    # Endpoints /products
│   │   └── StockRoutes.kt      # Endpoints /stock
│   └── plugins/
│       └── Plugins.kt          # Configuração Ktor (serialização, rotas, erros)
├── .env.example                # Template de variáveis de ambiente
├── .gitignore
└── build.gradle.kts
```
