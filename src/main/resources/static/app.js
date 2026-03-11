// ================================
// Agnes Chatbot frontend versão final
// ================================

const API = {
    chat: '/api/chat',
    conversations: '/api/conversas'
};

// State
let currentConversationId = null;
let isLoading = false;

// DOM Elements
const messagesContainer = document.getElementById('messages-container');
const messagesDiv = document.getElementById('messages');
const messageInput = document.getElementById('message-input');
const btnSend = document.getElementById('btn-send');
const btnNewChat = document.getElementById('btn-new-chat');
const btnMenu = document.getElementById('btn-menu');
const sidebar = document.getElementById('sidebar');
const overlay = document.getElementById('overlay');
const conversationList = document.getElementById('conversation-list');

// ========== inicialização ==========

document.addEventListener('DOMContentLoaded', () => {
    loadConversations();
    setupEventListeners();
});

function setupEventListeners() {
    btnSend.addEventListener('click', sendMessage);
    messageInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    });

    messageInput.addEventListener('input', () => {
        messageInput.style.height = 'auto';
        messageInput.style.height = Math.min(messageInput.scrollHeight, 160) + 'px';
        btnSend.disabled = messageInput.value.trim() === '';
    });

    btnNewChat.addEventListener('click', createNewConversation);
    btnMenu.addEventListener('click', toggleSidebar);
    overlay.addEventListener('click', toggleSidebar);
    bindSuggestionButtons();
    bindNamePrompt();
}

function bindSuggestionButtons() {
    document.querySelectorAll('.suggestion').forEach(btn => {

        if (btn.id === 'btn-submit-global-name') return;

        btn.addEventListener('click', () => {
            messageInput.value = btn.dataset.message;
            messageInput.dispatchEvent(new Event('input'));
            sendMessage();
        });
    });
}

function bindNamePrompt() {
    const inputName = document.getElementById('global-user-name');
    const btnSubmit = document.getElementById('btn-submit-global-name');
    const promptContainer = document.getElementById('global-name-prompt');
    const suggestionsContainer = document.getElementById('welcome-suggestions');

    if (!inputName || !btnSubmit) return;

    inputName.focus();
    messageInput.disabled = true;

    const submitName = () => {
        const name = inputName.value.trim();
        if (name) {

            const welcomeMsg = `Olá Agnes, meu nome é ${name}.`;
            promptContainer.style.display = 'none';
            suggestionsContainer.style.opacity = '1';
            suggestionsContainer.style.pointerEvents = 'auto';
            messageInput.disabled = false;
            messageInput.placeholder = "Escreva sua mensagem...";
            messageInput.focus();


            window.userGreetingContext = welcomeMsg;
        }
    };

    btnSubmit.addEventListener('click', submitName);
    inputName.addEventListener('keydown', (ev) => {
        if (ev.key === 'Enter') submitName();
    });
}



async function loadConversations() {
    try {
        const res = await fetch(API.conversations);
        const data = await res.json();
        const conversations = Array.isArray(data) ? data : (data.content || []);
        console.log("Conversations fetched:", conversations);
        renderConversations(conversations);
    } catch (err) {
        console.error('Erro ao carregar conversas:', err);
        const errDiv = document.createElement('div');
        errDiv.style.color = 'red';
        errDiv.style.padding = '10px';
        errDiv.innerText = 'Error loading: ' + err.message;
        document.getElementById('conversation-list').appendChild(errDiv);
    }
}

function renderConversations(conversations) {
    conversationList.innerHTML = '';

    if (conversations.length === 0) {
        conversationList.innerHTML = `
            <div style="padding: 20px; text-align: center; color: var(--text-tertiary); font-size: 12px;">
                Nenhuma conversa ainda
            </div>
        `;
        return;
    }

    conversations.forEach(conv => {
        try {
            const item = document.createElement('div');
            item.className = `conversation-item${conv.id === currentConversationId ? ' active' : ''}`;
            item.dataset.id = conv.id;
            item.innerHTML = `
                <span class="conversation-item-icon">💬</span>
                <span class="conversation-item-title">${escapeHtml(conv.title || 'Nova conversa')}</span>
                <div class="conversation-item-actions">
                    <button class="conversation-item-rename" title="Renomear conversa">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"></path>
                            <path d="m15 5 4 4"></path>
                        </svg>
                    </button>
                    <button class="conversation-item-delete" title="Excluir conversa">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="3 6 5 6 21 6"></polyline>
                            <path d="m19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                        </svg>
                    </button>
                </div>
            `;

            item.addEventListener('click', () => selectConversation(conv.id));

            item.querySelector('.conversation-item-rename').addEventListener('click', (e) => {
                e.stopPropagation();
                startRenameConversation(item, conv.id, conv.title || 'Nova conversa');
            });

            item.querySelector('.conversation-item-delete').addEventListener('click', (e) => {
                e.stopPropagation();
                deleteConversation(conv.id);
            });

            conversationList.appendChild(item);
        } catch (e) {
            console.error('Erro ao renderizar item da conversa:', e, conv);
        }
    });
}

async function startRenameConversation(item, convId, currentTitle) {
    const titleSpan = item.querySelector('.conversation-item-title');
    const actionsDiv = item.querySelector('.conversation-item-actions');


    actionsDiv.style.display = 'none';


    const input = document.createElement('input');
    input.type = 'text';
    input.value = currentTitle;
    input.className = 'conversation-rename-input';
    titleSpan.replaceWith(input);
    input.focus();
    input.select();

    async function saveRename() {
        const newTitle = input.value.trim();
        if (newTitle && newTitle !== currentTitle) {
            try {
                await fetch(`${API.conversations}/${convId}/title`, {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ title: newTitle })
                });
            } catch (err) {
                console.error('Erro ao renomear:', err);
            }
        }
        loadConversations();
    }

    function cancelRename() {
        loadConversations();
    }

    input.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
            saveRename();
        } else if (e.key === 'Escape') {
            cancelRename();
        }
    });

    input.addEventListener('blur', saveRename);
}

async function createNewConversation() {
    try {
        const res = await fetch(API.conversations, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({})
        });
        const conv = await res.json();
        currentConversationId = conv.id;
        showWelcome();
        loadConversations();
        closeSidebar();
        messageInput.focus();
    } catch (err) {
        console.error('Erro ao criar conversa:', err);
    }
}

async function selectConversation(id) {
    currentConversationId = id;
    closeSidebar();

    document.querySelectorAll('.conversation-item').forEach(el => {
        el.classList.toggle('active', parseInt(el.dataset.id) === id);
    });

    try {
        const res = await fetch(`/api/conversas/${currentConversationId}/mensagens?page=0&size=100`);
        const data = await res.json();
        const messages = Array.isArray(data) ? data : (data.content || []);

        if (messages.length === 0) {
            showWelcome();
        } else {
            hideWelcome();
            messagesDiv.innerHTML = '';
            messages.forEach(msg => appendMessage(msg.role, msg.content, msg.timestamp));
            scrollToBottom();
        }
    } catch (err) {
        console.error('Erro ao carregar mensagens:', err);
    }
}

async function deleteConversation(id) {
    try {
        await fetch(`${API.conversations}/${id}`, { method: 'DELETE' });
        if (currentConversationId === id) {
            currentConversationId = null;
            showWelcome();
        }
        loadConversations();
    } catch (err) {
        console.error('Erro ao excluir conversa:', err);
    }
}

// ========== Chat ==========

async function sendMessage() {
    const text = messageInput.value.trim();
    if (!text || isLoading) return;


    if (!currentConversationId) {
        try {
            const res = await fetch(API.conversations, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title: 'Nova conversa' })
            });
            const conv = await res.json();
            currentConversationId = conv.id;
        } catch (err) {
            console.error('Erro ao criar conversa:', err);
            return;
        }
    }

    hideWelcome();

    appendMessage('user', text);
    messageInput.value = '';
    messageInput.style.height = 'auto';
    btnSend.disabled = true;

    isLoading = true;
    showTypingIndicator();
    scrollToBottom();

    let payloadMessage = text;
    if (window.userGreetingContext) {
        payloadMessage = window.userGreetingContext + " " + text;
        window.userGreetingContext = null; // Consume the context
    }

    try {
        const res = await fetch(API.chat, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json', 'Accept': 'application/x-ndjson' },
            body: JSON.stringify({
                conversaId: currentConversationId,
                mensagem: payloadMessage
            })
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            throw new Error(errData.erro || 'Erro ao enviar mensagem');
        }

        removeTypingIndicator();

        const reader = res.body.getReader();
        const decoder = new TextDecoder('utf-8');
        let done = false;
        let streamFinished = false;
        let accumulatedText = '';
        let lastTimestamp = null;
        let buffer = '';

        let renderedParagraphsCount = 0;
        showTypingIndicator();

        let paragraphQueue = [];
        let queueProcessorResolve;
        const queueProcessorPromise = new Promise(r => queueProcessorResolve = r);

        async function processQueue() {
            while (!streamFinished || paragraphQueue.length > 0) {
                if (paragraphQueue.length > 0) {
                    removeTypingIndicator();
                    const pItem = paragraphQueue.shift();
                    const bubbleDiv = createAssistantBubble(pItem.text, pItem.isLast ? formatTime(lastTimestamp) : null);
                    messagesDiv.appendChild(bubbleDiv);
                    if (!pItem.isLast || !streamFinished) {
                        showTypingIndicator();
                    }
                    bindCatAnimations();
                    scrollToBottom();
                    await new Promise(r => setTimeout(r, 800));
                } else {
                    await new Promise(r => setTimeout(r, 50));
                }
            }
            queueProcessorResolve();
        }

        processQueue();

        while (!done) {
            const { value, done: readerDone } = await reader.read();
            done = readerDone;
            if (value) {
                buffer += decoder.decode(value, { stream: true });
                let boundary = buffer.indexOf('\n');
                while (boundary !== -1) {
                    const line = buffer.slice(0, boundary).trim();
                    buffer = buffer.slice(boundary + 1);
                    if (line) {
                        try {
                            const data = JSON.parse(line);
                            if (data.resposta) accumulatedText += data.resposta;
                            if (data.timestamp) lastTimestamp = data.timestamp;


                            const paragraphs = splitIntoParagraphs(accumulatedText);
                            const completeCount = paragraphs.length - 1;

                            while (renderedParagraphsCount < completeCount) {
                                paragraphQueue.push({ text: paragraphs[renderedParagraphsCount], isLast: false });
                                renderedParagraphsCount++;
                            }
                        } catch (e) {
                            console.error('Erro ao parsear JSON da stream:', line, e);
                        }
                    }
                    boundary = buffer.indexOf('\n');
                }
            }
        }

        streamFinished = true;


        const finalParagraphs = splitIntoParagraphs(accumulatedText);
        while (renderedParagraphsCount < finalParagraphs.length) {
            const isLast = renderedParagraphsCount === finalParagraphs.length - 1;
            paragraphQueue.push({ text: finalParagraphs[renderedParagraphsCount], isLast: isLast });
            renderedParagraphsCount++;
        }

        await queueProcessorPromise;
        removeTypingIndicator();

        loadConversations();
    } catch (err) {
        removeTypingIndicator();
        appendMessage('assistant', `Ops, algo deu errado 😅 ${err.message || 'Tente novamente.'}`);
        console.error('Chat error:', err);
    } finally {
        isLoading = false;
        scrollToBottom();
        messageInput.focus();
    }
}

// ========== Renderização de Mensagens ==========

const CAT_AVATAR = `<div class="b"><svg viewBox="0 0 150 150" class="used"><use xlink:href="#theHex" class="theHex" fill="#ffca5e" /><use xlink:href="#_lb_wisker" class="use_lb" width="110" x="20" y="15" /><use xlink:href="#_lt_wisker" class="use_lt" width="110" x="20" y="15" /><use xlink:href="#_rb_wisker" class="use_rb" width="110" x="20" y="15" /><use xlink:href="#_rt_wisker" class="use_rt" width="110" x="20" y="15" /><use xlink:href="#eyes" class="use_eyes" width="110" x="20" y="15" /><path class="tongue" d="M75 97H65A5,10 0 0 0 85,97z" /><use xlink:href="#thecat" width="110" x="20" y="15" /></svg></div>`;

/**
 * Adiciona uma mensagem (para histórico ou mensagens do usuário, sem animação).
 */
function appendMessage(role, content, timestamp) {
    if (role === 'assistant') {
        appendAssistantBubbles(content, timestamp, false);
    } else {
        const div = document.createElement('div');
        div.className = `message ${role}`;
        const time = formatTime(timestamp);
        div.innerHTML = `
            <div class="message-avatar">👤</div>
            <div class="message-content">
                <div class="message-bubble">${escapeHtml(content)}</div>
                <div class="message-time">${time}</div>
            </div>
        `;
        messagesDiv.appendChild(div);
        scrollToBottom();
    }
}

/**
 * Adiciona resposta do assistente com animação de digitação.
 * Chamado após receber uma nova resposta da IA.
 */
function appendAssistantAnimated(content, timestamp) {
    appendAssistantBubbles(content, timestamp, true);
}

/**
 * Divide o conteúdo do assistente em parágrafos e renderiza cada um separadamente.
 * Se animated=true, digita cada balão sequencialmente.
 */
function appendAssistantBubbles(content, timestamp, animated) {
    const paragraphs = splitIntoParagraphs(content);
    const time = formatTime(timestamp);

    if (!animated) {
        // Renderização instantânea (carregamento de histórico)
        paragraphs.forEach((para, idx) => {
            const div = createAssistantBubble(para, idx === paragraphs.length - 1 ? time : null);
            messagesDiv.appendChild(div);
        });
        bindCatAnimations();
        scrollToBottom();
        return;
    }

    // Animado: exibe balões um a um com efeito de pulo (pop)
    let bubbleIdx = 0;

    function showNextBubble() {
        if (bubbleIdx >= paragraphs.length) {
            bindCatAnimations();
            return;
        }

        const para = paragraphs[bubbleIdx];
        const isLast = bubbleIdx === paragraphs.length - 1;
        const div = createAssistantBubble(para, isLast ? time : null);
        messagesDiv.appendChild(div);

        // A animação CSS bubblePopLeft cuida do efeito de pop automaticamente
        scrollToBottom();

        bubbleIdx++;
        if (bubbleIdx < paragraphs.length) {
            // Atraso antes do próximo balão aparecer
            setTimeout(showNextBubble, 1000);
        } else {
            bindCatAnimations();
        }
    }

    showNextBubble();
}

function createAssistantBubble(content, timeStr) {
    const div = document.createElement('div');
    div.className = 'message assistant';
    div.innerHTML = `
        <div class="message-avatar">${CAT_AVATAR}</div>
        <div class="message-content">
            <div class="message-bubble">${content ? formatMessageContent(content) : ''}</div>
            ${timeStr ? `<div class="message-time">${timeStr}</div>` : ''}
        </div>
    `;
    return div;
}

/**
 * Divide o conteúdo em parágrafos lógicos para balões separados.
 * Mantém blocos de código juntos, dividindo em quebras duplas de linha.
 */
function splitIntoParagraphs(content) {
    if (!content) return [''];

    // Protege blocos de código de serem divididos
    const codeBlocks = [];
    let text = content.replace(/```[\s\S]*?(?:```|$)/g, (match) => {
        const idx = codeBlocks.length;
        codeBlocks.push(match);
        return `\x00CODE_${idx}\x00`;
    });

    // Divide em quebras duplas de linha (quebras de parágrafo)
    let parts = text.split(/\n{2,}/);

    // Restaura blocos de código e remove vazios
    parts = parts.map(p => {
        codeBlocks.forEach((block, idx) => {
            p = p.replace(`\x00CODE_${idx}\x00`, block);
        });
        return p.trim();
    }).filter(p => p.length > 0);

    // Se não houver divisões, retorna tudo em um balão apenas
    if (parts.length === 0) return [content];

    return parts;
}

// ========== Formatador de Markdown ==========

function formatMessageContent(content) {
    if (!content) return '';

    let text = content;

    // 1. Extrai blocos de código (protege de outras formatações)
    const codeBlocks = [];
    text = text.replace(/```(\w*)\n?([\s\S]*?)(?:```|$)/g, (_, lang, code) => {
        const idx = codeBlocks.length;
        const langLabel = lang ? `<span class="code-lang">${escapeHtml(lang)}</span>` : '';
        codeBlocks.push(`<div class="code-block">${langLabel}<pre><code>${escapeHtml(code.trim())}</code></pre></div>`);
        return `\x00CODEBLOCK_${idx}\x00`;
    });

    // 2. Extrai código inline
    const inlineCodes = [];
    text = text.replace(/`([^`]+)`/g, (_, code) => {
        const idx = inlineCodes.length;
        inlineCodes.push(`<code class="inline-code">${escapeHtml(code)}</code>`);
        return `\x00INLINE_${idx}\x00`;
    });

    // 3. Aplica escape HTML para o restante
    text = escapeHtml(text);

    // 4. Cabeçalhos (#### > ### > ## > #)
    text = text.replace(/^####\s+(.+)$/gm, '<h4 class="md-heading md-h3">$1</h4>');
    text = text.replace(/^###\s+(.+)$/gm, '<h4 class="md-heading md-h3">$1</h4>');
    text = text.replace(/^##\s+(.+)$/gm, '<h3 class="md-heading md-h2">$1</h3>');
    text = text.replace(/^#\s+(.+)$/gm, '<h2 class="md-heading md-h1">$1</h2>');

    // 5. Linhas horizontais
    text = text.replace(/^[-*_]{3,}\s*$/gm, '<hr class="md-hr">');

    // 6. Listas não ordenadas (- item ou * item)
    text = text.replace(/^\s*[\-\*]\s+(.+)$/gm, '<li>$1</li>');
    // Nós devemos dar match em quaisquer blocos <li> que podem estar separados por quebras de linha ou <br>s
    text = text.replace(/(?:<li>.*?<\/li>\s*(?:<br>\s*)*)+/g, match => `<ul class="md-list">${match}</ul>`);

    // 7. Listas ordenadas (1. item)
    text = text.replace(/^\s*\d+\.\s+(.+)$/gm, '<li>$1</li>');
    text = text.replace(/(?:<li>.*?<\/li>\s*(?:<br>\s*)*)+/g, match => {
        // Se já contiver o fechamento de ul, não afeta.
        if (match.includes('<ul')) return match;
        return `<ol class="md-list">${match}</ol>`;
    });

    // 8. Negrito (**...**)
    text = text.replace(/\*\*([^*]+)\*\*/g, '<strong class="md-bold">$1</strong>');

    // 9. Itálico (*...*)
    text = text.replace(/\*([^*]+)\*/g, '<em class="md-italic">$1</em>');

    // 10. Indicadores de Nível (badges coloridas)
    text = text.replace(/🟢/g, '<span class="level-badge level-junior">🟢</span>');
    text = text.replace(/🟡/g, '<span class="level-badge level-pleno">🟡</span>');
    text = text.replace(/🔵/g, '<span class="level-badge level-advanced">🔵</span>');

    // 11. Quebras de linha
    text = text.replace(/\n/g, '<br>');

    // 12. Limpa tags <br> ao redor de elementos do bloco
    text = text.replace(/<br>\s*(<h[234])/g, '$1');
    text = text.replace(/(<\/h[234]>)\s*<br>/g, '$1');
    text = text.replace(/<br>\s*(<hr)/g, '$1');
    text = text.replace(/(<\/ul>)\s*<br>/g, '$1');
    text = text.replace(/<br>\s*(<ul)/g, '$1');
    text = text.replace(/<br>\s*(<div class="code-block")/g, '$1');

    // 13. Restaura blocos de código e código inline
    codeBlocks.forEach((block, idx) => {
        text = text.replace(`\x00CODEBLOCK_${idx}\x00`, block);
    });
    inlineCodes.forEach((code, idx) => {
        text = text.replace(`\x00INLINE_${idx}\x00`, code);
    });

    return text;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ========== Indicador de Digitação ==========

function showTypingIndicator() {
    const div = document.createElement('div');
    div.className = 'typing-indicator';
    div.id = 'typing-indicator';
    div.innerHTML = `
        <div class="message-avatar">
            ${CAT_AVATAR}
        </div>
        <div class="typing-dots">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
        </div>
    `;
    messagesDiv.appendChild(div);
    bindCatAnimations();
    scrollToBottom();
}

function removeTypingIndicator() {
    const typing = document.getElementById('typing-indicator');
    if (typing) typing.remove();
}

// ========== Helpers de UI ==========

function showWelcome() {
    messagesDiv.innerHTML = `
        <div class="welcome" id="welcome">
            <div class="welcome-icon">
                <div class="b">
                    <svg viewBox="0 0 150 150" class="used">
                        <use xlink:href="#theHex" class="theHex" fill="#ffae3e" />
                        <use xlink:href="#_lb_wisker" class="use_lb" width="110" x="20" y="15" />
                        <use xlink:href="#_lt_wisker" class="use_lt" width="110" x="20" y="15" />
                        <use xlink:href="#_rb_wisker" class="use_rb" width="110" x="20" y="15" />
                        <use xlink:href="#_rt_wisker" class="use_rt" width="110" x="20" y="15" />
                        <use xlink:href="#eyes" class="use_eyes" width="110" x="20" y="15" />
                        <path class="tongue" d="M75 97H65A5,10 0 0 0 85,97z" />
                        <use xlink:href="#thecat" width="110" x="20" y="15" />
                    </svg>
                </div>
            </div>
            <h2 class="welcome-title">Olá! Eu sou a Agnes</h2>
            <p class="welcome-text">Sua mentora de Java, do júnior ao pleno. Pode me perguntar qualquer coisa sobre Java, Spring Boot, boas práticas e muito mais!</p>
            <div class="welcome-suggestions">
                <button class="suggestion btn-learn" data-message="Sou iniciante em Java, por onde começo?">Começar em Java</button>
                <button class="suggestion" data-message="Me explica como funciona Spring Boot">Spring Boot</button>
                <button class="suggestion" data-message="Quais padrões de projeto são mais usados em Java?">Design Patterns</button>
                <button class="suggestion" data-message="Me ajuda a entender Stream API com exemplos">Stream API</button>
            </div>
        </div>
    `;
    bindSuggestionButtons();
    bindCatAnimations();
}

function hideWelcome() {
    const w = document.getElementById('welcome');
    if (w) w.remove();
}

function scrollToBottom() {
    requestAnimationFrame(() => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    });
}

function toggleSidebar() {
    sidebar.classList.toggle('open');
    overlay.classList.toggle('visible');
}

function closeSidebar() {
    sidebar.classList.remove('open');
    overlay.classList.remove('visible');
}

function formatTime(timestamp) {
    if (!timestamp) {
        return new Date().toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    }
    try {
        let ts = String(timestamp);
        if (!ts.includes('Z') && !ts.includes('+') && !ts.includes('-', 10)) {
            const parts = ts.split('T');
            if (parts.length === 2) {
                const [h, m] = parts[1].split(':');
                return `${h.padStart(2, '0')}:${m.padStart(2, '0')}`;
            }
        }
        const date = new Date(ts);
        if (isNaN(date.getTime())) return '';
        return date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
    } catch {
        return '';
    }
}

// ========== Lógica de Animação do Gato ==========

let rid = null;
let cats = [];

class Path {
    constructor(path, hex) {
        this.memory = [1, 10];
        this.target = this.memory[0];
        this.value = this.memory[1];
        this.path = path;
        this.hex = hex;
    }

    updatePath() {
        this.path.setAttributeNS(
            null,
            "d",
            `M75 97H65A5,${this.value} 0 0 0 85,97z`
        );
    }

    updateValue() {
        let dist = this.target - this.value;
        let vel = dist / 10;
        this.value += vel;
    }
}

function bindCatAnimations() {
    cats = []; // limpa a array para evitar duplicações quando fizer bind novamente
    const svgs = Array.from(document.querySelectorAll("svg.used"));

    svgs.forEach(svg => {
        const tongue = svg.querySelector(".tongue");
        const hex = svg.querySelector(".theHex");

        // Verifica a preexistência dos elementos (caso a DOM não esteja totalmente pronta)
        if (!tongue || !hex) return;

        // Evita a duplicação dos eventos ouvintes ao verificar as flags no dataset
        if (!hex.dataset.bound) {
            hex.dataset.bound = "true";
            const p = new Path(tongue, hex);
            cats.push(p);

            hex.addEventListener("mouseover", () => {
                p.target = p.memory[1];
                p.memory.reverse();
            });

            hex.addEventListener("mouseleave", () => {
                p.target = p.memory[1];
                p.memory.reverse();
            });

            // Add a touch listener for mobile tap to animate
            hex.addEventListener("touchstart", () => {
                p.target = p.memory[1];
                p.memory.reverse();
                setTimeout(() => {
                    p.target = p.memory[1];
                    p.memory.reverse();
                }, 300); // revert after 300ms
            });
        } else {
            // Just add to cats array for animation loop if already bound
            const p = new Path(tongue, hex);
            cats.push(p);
        }
    });
}

function Frame() {
    rid = window.requestAnimationFrame(Frame);

    cats.forEach(p => {
        p.updateValue();
        p.updatePath();
    });
}

// Initial bind
setTimeout(() => {
    bindCatAnimations();
    Frame();
}, 100);
