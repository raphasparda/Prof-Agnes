$packages = @(
    @("package com.agnes.controller", "package com.agnes.controlador"),
    @("package com.agnes.service", "package com.agnes.servico"),
    @("package com.agnes.repository", "package com.agnes.repositorio"),
    @("package com.agnes.model", "package com.agnes.modelo"),
    @("package com.agnes.config", "package com.agnes.configuracao"),
    @("import com.agnes.controller", "import com.agnes.controlador"),
    @("import com.agnes.service", "import com.agnes.servico"),
    @("import com.agnes.repository", "import com.agnes.repositorio"),
    @("import com.agnes.model", "import com.agnes.modelo"),
    @("import com.agnes.config", "import com.agnes.configuracao")
)

$words = @(
    @("ChatController", "ControladorChat"),
    @("ConversationController", "ControladorConversa"),
    @("GlobalExceptionHandler", "ManipuladorExcecaoGlobal"),
    @("ChatService", "ServicoChat"),
    @("ConversationService", "ServicoConversa"),
    @("PromptGuardService", "ServicoGuardaFiltro"),
    @("ConversationRepository", "RepositorioConversa"),
    @("MessageRepository", "RepositorioMensagem"),
    @("Conversation", "Conversa"),
    @("Message", "Mensagem"),
    @("ChatRequest", "RequisicaoChat"),
    @("ChatResponse", "RespostaChat"),
    
    @("chatController", "controladorChat"),
    @("conversationController", "controladorConversa"),
    @("globalExceptionHandler", "manipuladorExcecaoGlobal"),
    @("chatService", "servicoChat"),
    @("conversationService", "servicoConversa"),
    @("promptGuardService", "servicoGuardaFiltro"),
    @("conversationRepository", "repositorioConversa"),
    @("messageRepository", "repositorioMensagem"),
    @("conversation", "conversa"),
    @("message", "mensagem"),
    @("chatRequest", "requisicaoChat"),
    @("chatResponse", "respostaChat"),
    
    @("conversations", "conversas"),
    @("messages", "mensagens")
)

$files = Get-ChildItem -Path "d:\chatbot\src" -Recurse -Filter "*.java"

foreach ($file in $files) {
    if ($file.FullName -match "target\\") { continue }
    $content = Get-Content $file.FullName -Raw
    
    foreach ($pair in $packages) {
        $content = $content.Replace($pair[0], $pair[1])
    }
    
    foreach ($pair in $words) {
        $content = [regex]::Replace($content, "\b$($pair[0])\b", $pair[1])
    }
    
    Set-Content -Path $file.FullName -Value $content -Encoding UTF8
}
