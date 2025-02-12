# Self Service API - Backend

## 🎯 Visão Geral
Sistema backend para um serviço self-service completo, desenvolvido em Java utilizando arquitetura limpa (Clean Architecture) e princípios SOLID para garantir um código desacoplado e manutenível. (PARA ESTUDO)

## 🏗️ Arquitetura do Projeto

### Camadas da Arquitetura Limpa
1. **Entities (Domain)**
   - Regras de negócio corporativas
   - Objetos de domínio
   - Value Objects
   - Interfaces dos repositórios

2. **Use Cases (Application)**
   - Regras de negócio da aplicação
   - Implementação dos casos de uso
   - DTOs
   - Interfaces dos serviços

3. **Interface Adapters**
   - Controllers
   - Presenters
   - Gateways
   - Implementações dos repositórios

4. **Frameworks & Drivers**
   - Configurações do Spring
   - Implementações de banco de dados
   - Serviços externos
   - Configurações de segurança

## 🛠️ Stack Tecnológica

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway (Migrations)
- Maven
- JUnit 5
- Mockito
- Swagger/OpenAPI
- Docker
- Docker Compose

## 📋 Funcionalidades Principais

1. **Gestão de Usuários**
   - Cadastro
   - Autenticação
   - Autorização (RBAC)
   - Perfis de acesso

2. **Catálogo de Produtos**
   - CRUD de produtos
   - Categorias
   - Preços
   - Disponibilidade

3. **Gestão de Pedidos**
   - Criação de pedidos
   - Acompanhamento de status
   - Histórico
   - Notificações

4. **Pagamentos**
   - Integração com gateway de pagamento
   - Múltiplas formas de pagamento
   - Histórico de transações

5. **Relatórios e Analytics**
   - Relatórios de vendas
   - Métricas de desempenho
   - Dashboard administrativo

## 🗺️ Roadmap de Implementação

### Fase 1: Setup Inicial
1. Configuração do projeto Spring Boot
2. Implementação da estrutura de Clean Architecture
3. Configuração do banco de dados
4. Setup do Docker
5. Configuração do Swagger

### Fase 2: Core Features
1. Implementação do domínio principal
2. Desenvolvimento dos casos de uso básicos
3. Criação dos endpoints REST
4. Implementação da camada de persistência

### Fase 3: Segurança e Autenticação
1. Implementação do Spring Security
2. JWT Authentication
3. RBAC (Role-Based Access Control)
4. Proteção de endpoints

### Fase 4: Features Avançadas
1. Implementação do sistema de pedidos
2. Integração com gateway de pagamento
3. Sistema de notificações
4. Relatórios e métricas

### Fase 5: Qualidade e DevOps
1. Testes unitários
2. Testes de integração
3. CI/CD Pipeline
4. Monitoramento e logs

## 🚀 Como Executar

### Pré-requisitos
- Java 17
- Docker
- Maven
- PostgreSQL (ou Docker)

### Passos para Execução
1. Clone o repositório
2. Configure as variáveis de ambiente
3. Execute o Docker Compose
4. Execute as migrations
5. Inicie a aplicação

```bash
# Clone o repositório
git clone [url-do-repositorio]

# Entre no diretório
cd self-service-backend

# Build do projeto
mvn clean install

# Execute o Docker Compose
docker-compose up -d

# Execute a aplicação
mvn spring-boot:run
```

## 📝 Convenções e Boas Práticas

1. **Código**
   - Seguir princípios SOLID
   - Clean Code
   - Documentação clara
   - Testes automatizados

2. **Git**
   - Conventional Commits
   - Feature Branches
   - Pull Requests
   - Code Review

3. **Documentação**
   - Swagger/OpenAPI
   - Javadoc
   - README atualizado
   - Documentação de arquitetura

# Criar um produto

curl -X POST http://localhost:8080/api/produtos \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Produto Teste",
    "descricao": "Descrição do produto teste",
    "preco": 99.90
  }'

# Listar todos os produtos

curl http://localhost:8080/api/produtos

# Buscar um produto específico
curl http://localhost:8080/api/produtos/1
