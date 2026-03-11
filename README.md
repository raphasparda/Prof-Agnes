<div align="center">

# Prof. Agnes

**Mentora de Java com IA — do júnior ao pleno.**

<br>

![Java](https://img.shields.io/badge/Java_17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Gemini](https://img.shields.io/badge/Gemini_2.5_Flash-4285F4?style=for-the-badge&logo=google&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

<br>

<img src="Readme/Gif Agnes apresentação.gif" alt="Demo da Agnes" width="90%">

<br>

</div>

---

## Sobre o projeto

Agnes é uma mentora de Java com inteligência artificial. Diferente de chatbots genéricos que tentam responder sobre qualquer coisa, ela foi projetada com um único propósito: ajudar desenvolvedores Java a evoluir do nível júnior ao pleno.

O nome vem de homenagem — e a personalidade também. Ela é acolhedora, tem paciência de sobra, e sabe dosar entre explicação didática e profundidade técnica. Se o aluno manda um "o que é POO?", ela explica com analogias. Se manda "me explica type erasure em generics", ela vai fundo.

A ideia nasceu de uma frustração real: conteúdo de Java na internet é disperso, genérico e geralmente ignora o contexto de quem está perguntando. Agnes resolve isso sendo uma fonte centralizada que se adapta ao nível de quem conversa com ela.

---

## Features

<table>
<tr>
<td width="50%">

### <img src="https://img.shields.io/badge/-Inteligência-4285F4?style=flat-square" alt="ai"> IA Especializada

- Motor de IA: **Google Gemini 2.5 Flash** via Spring AI
- Respostas em **streaming reativo** (token por token, sem espera)
- System prompt com **15 domínios de Java** mapeados
- Auto-titulação de conversas baseada no conteúdo

</td>
<td width="50%">

### <img src="https://img.shields.io/badge/-Segurança-E53935?style=flat-square" alt="security"> Anti-Prompt Injection

- Serviço dedicado com 3 níveis de ameaça: `SAFE`, `SUSPICIOUS`, `BLOCKED`
- 10+ padrões regex bilíngues (PT-BR e EN)
- Detecção de role hijacking, jailbreak, delimiter injection
- Normalização contra ofuscação Unicode e zero-width chars

</td>
</tr>
<tr>
<td width="50%">

### <img src="https://img.shields.io/badge/-Interface-FF9800?style=flat-square" alt="ui"> Chat Premium

- Markdown rendering: cabeçalhos, listas, **code blocks com syntax highlight**
- Animação de digitação com balões sequenciais
- Histórico persistente com renomear e excluir
- Mascote SVG interativa com animações de hover
- Layout responsivo (desktop e mobile)

</td>
<td width="50%">

### <img src="https://img.shields.io/badge/-Arquitetura-6DB33F?style=flat-square" alt="arch"> Código Limpo

- Camadas: `controlador` → `servico` → `repositorio` → `modelo`
- DTOs dedicados, sem vazamento de entidades na API
- Exception handler global com respostas padronizadas
- Dual-database: H2 local / PostgreSQL (Neon) em produção
- Dockerfile multi-stage otimizado

</td>
</tr>
</table>

---

## O que faz ela diferente

A maioria dos projetos de portfólio com IA são wrappers genéricos que repassam prompts sem nenhum tratamento. Agnes foi pensada como produto:

**Escopo fechado** — ela só fala de Java. Perguntou de Python? Ela recusa educadamente e redireciona. Isso não é limitação, é decisão de design. Um mentor especialista é mais útil que um generalista raso.

**Adaptação ao nível** — cada tópico é classificado com indicadores de senioridade (Júnior, Pleno, Avançado). Ela calibra a profundidade da resposta pelo nível que o aluno demonstra na conversa.

**Segurança de verdade** — o `ServicoGuardaFiltro` não é um if/else. É um serviço completo com regex patterns bilíngues, normalização de input, e logging de auditoria. Tentativas de jailbreak são bloqueadas silenciosamente sem expor informações.

**Persistência real** — conversas ficam salvas em PostgreSQL. O aluno pode voltar dias depois e continuar de onde parou. O contexto é reconstruído com as últimas 20 mensagens do histórico.

---

## Público-alvo

| Perfil | O que ele ganha |
|---|---|
| Estudante começando em Java | Explicações do zero, com exemplos compiláveis e analogias |
| Dev júnior migrando pra pleno | Profundidade em Spring Boot, JPA, Design Patterns, SOLID |
| Quem está se preparando pra entrevista | Cobertura dos tópicos clássicos: Collections, Streams, concorrência |
| Autodidata que se perde em tutoriais | Uma fonte centralizada que lembra do contexto |

---

## Arquitetura

```
┌──────────────────────────────────────────────────────┐
│                   Frontend (SPA)                      │
│          HTML + CSS + JS vanilla + SVG animado         │
└───────────────────────┬──────────────────────────────┘
                        │ HTTP / NDJSON Stream
┌───────────────────────▼──────────────────────────────┐
│                  Spring Boot 3.4                      │
│                                                       │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐  │
│  │ Controlador │→ │   Serviço   │→ │ Repositório  │  │
│  │  (REST API) │  │ (Chat/Conv) │  │    (JPA)     │  │
│  └─────────────┘  └──────┬──────┘  └──────┬───────┘  │
│                          │                 │          │
│  ┌───────────────────────▼──┐    ┌─────────▼────────┐│
│  │  Anti-Prompt Injection   │    │   PostgreSQL /   ││
│  │  Guard                   │    │   H2 Database    ││
│  └───────────────────────┬──┘    └──────────────────┘│
│                          │                            │
│  ┌───────────────────────▼──────────────────────────┐│
│  │         Spring AI → Google Gemini 2.5 Flash      ││
│  └──────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────┘
```

---

## Stack

```
Backend                          Frontend
├── Java 17+                     ├── HTML5 semântico
├── Spring Boot 3.4              ├── CSS3 (custom properties, animações)
├── Spring AI (GenAI Starter)    ├── JavaScript vanilla (ES6+)
├── Spring Data JPA / Hibernate  ├── SVG interativo
├── Project Reactor (Flux)       └── Markdown parser customizado
├── PostgreSQL / H2
└── Maven                        Infra
                                 ├── Docker (multi-stage)
                                 ├── Render
                                 └── Neon.tech (PostgreSQL serverless)
```

---

## Cobertura de domínio

Agnes cobre 15 áreas do ecossistema Java, cada uma com profundidade progressiva:

| Domínio | Nível |
|---|---|
| Java Core (tipos, operadores, controle de fluxo) | Júnior |
| POO (herança, polimorfismo, encapsulamento) | Júnior |
| Collections Framework | Júnior → Pleno |
| Java Moderno (Lambdas, Streams, Records, Sealed Classes) | Pleno |
| Tratamento de Exceções | Júnior → Pleno |
| Generics (wildcards, bounded types, type erasure) | Pleno |
| Concorrência (threads, Virtual Threads, CompletableFuture) | Pleno → Avançado |
| I/O e NIO | Pleno |
| JDBC, JPA e Hibernate | Pleno → Avançado |
| Spring Framework e Spring Boot | Pleno → Avançado |
| Testes (JUnit 5, Mockito, TDD) | Pleno |
| Padrões de Projeto (GoF, DTO, Repository) | Pleno → Avançado |
| Arquitetura (SOLID, Clean Architecture, Microsserviços) | Avançado |
| Ferramentas (Maven, Gradle, Git, Docker) | Júnior → Pleno |
| Banco de Dados (SQL, modelagem, performance) | Pleno → Avançado |

---

<div align="center">

Desenvolvido por [sparda.DEV](https://github.com/raphasparda)

*Agnes pode cometer erros. Sempre verifique informações importantes.*

</div>
