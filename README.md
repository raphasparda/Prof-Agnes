<div align="center">

# Prof. Agnes `v1.0`

**Mentora de Java com IA. Do júnior ao pleno.**

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

Agnes é uma mentora de Java com inteligência artificial. Diferente de chatbots genéricos que respondem sobre qualquer coisa, ela foi construída com um propósito só: ajudar quem quer aprender Java de verdade, desde o primeiro Hello World até arquiteturas profissionais.

O projeto nasceu pensando em três perfis: o **estudante** que está começando e não sabe por onde ir, o **profissional** da área que quer evoluir tecnicamente, e a **pessoa curiosa** que ouviu falar de Java e quer entender do que se trata. Agnes recebe todo mundo do mesmo jeito: com paciência.

---

## De onde vem o nome e o visual

A paleta de cores, a personalidade acolhedora e o mascote hexagonal foram todos inspirados na minha gata de estimação. A escolha foi proposital. O mundo tech tem uma tendência forte de visual agressivo, temas escuros, interfaces intimidadoras. Agnes vai na direção oposta: tons quentes, traços suaves e uma identidade que passa conforto antes de qualquer outra coisa.

A ideia é que quem abre a plataforma se sinta convidado a ficar. Quem está começando em programação já carrega insegurança o suficiente. O ambiente de estudo não precisa contribuir pra isso.

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

A maioria dos projetos de portfólio com IA são wrappers que repassam prompts crus e devolvem o resultado. Agnes foi pensada como produto.

**Escopo fechado.** Ela só fala de Java. Perguntou de Python? Ela recusa educadamente e redireciona. Isso não é limitação, é decisão de design. Um mentor especialista é mais útil que um generalista raso.

**Adaptação ao nível.** Cada tópico é classificado com indicadores de senioridade (Júnior, Pleno, Avançado). Ela calibra a profundidade da resposta pelo nível que o aluno demonstra na conversa.

**Segurança de verdade.** O `ServicoGuardaFiltro` não é um if/else. É um serviço completo com regex bilíngues, normalização de input e logging de auditoria. Tentativas de jailbreak são bloqueadas silenciosamente sem expor informações.

**Persistência real.** Conversas ficam salvas em PostgreSQL. O aluno pode voltar dias depois e continuar de onde parou. O contexto é reconstruído com as últimas 20 mensagens do histórico.

---

## Público-alvo

| Perfil | O que ele ganha |
|---|---|
| Estudante começando em Java | Explicações do zero, com exemplos compiláveis e analogias |
| Dev júnior querendo virar pleno | Profundidade em Spring Boot, JPA, Design Patterns, SOLID |
| Quem está se preparando pra entrevista | Cobertura dos tópicos clássicos: Collections, Streams, concorrência |
| Pessoa curiosa sobre Java | Uma porta de entrada acessível e sem jargões intimidadores |

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

## Roadmap: o que vem na v2.0

A versão 1.0 entrega a experiência core: conversar com a Agnes, persistir histórico e ter segurança contra prompt injection. Mas analisando a estrutura atual, já dá pra ver onde o projeto pode crescer.

**Autenticação de usuários.** Hoje qualquer pessoa que acessa a URL compartilha o mesmo espaço de conversas. Na v2.0, cada aluno teria seu login (OAuth2 com Google/GitHub via Spring Security), com conversas privadas e perfil próprio.

**Painel de progresso.** Com o histórico persistido no banco, já existe a base de dados pra construir um dashboard que mostre quais domínios o aluno mais explorou, quais ainda não tocou, e sugerir próximos passos. Uma espécie de trilha de aprendizado gerada automaticamente.

**Modo quiz.** Agnes já sabe adaptar o nível da resposta. O próximo passo natural é usar isso pra gerar quizzes interativos: perguntas de múltipla escolha, desafios de código, e exercícios práticos com correção automática.

**Exportação de conversas.** Permitir que o aluno exporte suas conversas como PDF ou Markdown, pra usar como material de estudo offline ou como anotações pessoais.

**Suporte a múltiplos modelos.** A arquitetura com Spring AI já abstrai o provedor de IA. Na v2.0, o aluno poderia escolher entre diferentes modelos (Gemini, Claude, GPT) dependendo da necessidade, ou o sistema alternaria automaticamente em caso de indisponibilidade.

**Modo colaborativo.** Permitir que dois alunos compartilhem uma sessão de conversa com a Agnes, útil para estudo em dupla ou pair programming assistido.

---

<div align="center">

Desenvolvido por [sparda.DEV](https://github.com/raphasparda)

</div>
