package com.agnes.servico;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Anti-Prompt Injection Guard.
 * Analyzes user mensagens before they reach the AI model,
 * detecting and blocking prompt injection attempts.
 */
@Service
public class ServicoGuardaFiltro {

    private static final Logger log = LoggerFactory.getLogger(ServicoGuardaFiltro.class);

    public enum ThreatLevel {
        SAFE, SUSPICIOUS, BLOCKED
    }

    public record GuardResult(ThreatLevel level, String reason) {
        public boolean isBlocked() {
            return level == ThreatLevel.BLOCKED;
        }
    }

    // â”€â”€ BLOCKED patterns (high confidence injection attempts) â”€â”€

    private static final List<Pattern> BLOCKED_PATTERNS = List.of(
            // EN: Override / forget instructions
            ci("(?:ignore|forget|disregard|override|bypass|skip|drop)\\s+(?:all|your|the|any|previous|above|prior|earlier|old)\\s+(?:instructions?|rules?|prompts?|directives?|guidelines?|constraints?)"),

            // EN: Role hijacking
            ci("(?:you\\s+are\\s+now|from\\s+now\\s+on\\s+you\\s+are|act\\s+as\\s+if\\s+you\\s+were|pretend\\s+(?:to\\s+be|you\\s*(?:'re|are))|behave\\s+as|roleplay\\s+as|switch\\s+to\\s+(?:being|acting))"),

            // EN: Reveal system prompt
            ci("(?:reveal|show|display|print|output|repeat|echo|dump|list|give\\s+me|tell\\s+me)\\s+(?:your|the)\\s+(?:system|initial|original|internal|hidden|full|complete|entire)\\s+(?:prompt|instructions?|rules?|mensagem|configuration|directives?)"),

            // EN: Jailbreak keywords
            ci("\\b(?:DAN|do\\s+anything\\s+now|jailbreak|evil\\s+mode|developer\\s+mode|god\\s+mode|unrestricted\\s+mode|no\\s+filter)\\b"),

            // PT-BR: Ignorar/esquecer instruções
            ci("(?:ignore|ignor[ea]|esque[cç]a|desconsider[ea]|abandon[ea]|descartar?|sobrescrev[ea]|pul[ea])\\s+(?:todas?\\s+as|todas?\\s+suas|todas?|suas?|as|os|o|qualquer)\\s+(?:instru[cç][oõ]es|regras?|prompts?|diretrizes?|configura[cç][oõ]es)"),

            // PT-BR: Role hijacking
            ci("(?:voc[eê]\\s+agora\\s+[eé]|a\\s+partir\\s+de\\s+agora\\s+(?:voc[eê]\\s+[eé]|seja)|finja\\s+(?:ser|que\\s+[eé])|se\\s+comporte\\s+como|mude\\s+(?:sua|de)\\s+(?:personalidade|identidade|papel))"),

            // PT-BR: Reveal prompt
            ci("(?:mostr[ea]|revel[ea]|exib[ea]|imprim[ea]|diga|fal[ea]|repita|me\\s+(?:mostr[ea]|diga|fal[ea]))\\s+(?:seu|o|suas?)\\s+(?:prompt|instru[cç][oõ]es|regras|sistema|configura[cç][aã]o)\\s*(?:de\\s+sistema|internos?|originai?s?|inicial|ocultos?)?"),

            // Delimiter injection (trying to insert system-like tags)
            ci("<\\s*/?\\s*(?:system|instruction|prompt|role|assistant|admin|root)\\s*>"),
            ci("\\[\\s*(?:SYSTEM|INSTRUCTION|NEW\\s*ROLE|ADMIN|OVERRIDE|RESET)\\s*\\]"),
            ci("###\\s*(?:INSTRUCTION|SYSTEM|OVERRIDE|NEW\\s*ROLE|RESET)"));

    // â”€â”€ SUSPICIOUS patterns (might be legitimate but warrant logging) â”€â”€

    private static final List<Pattern> SUSPICIOUS_PATTERNS = List.of(
            // Asking about the prompt indirectly
            ci("(?:quais?|qual|what)\\s+(?:s[aã]o|[eé]|are)\\s+(?:suas?|your)\\s+(?:instru[cç][oõ]es|regras?|instructions?|rules?)"),

            // Trying to get meta information
            ci("(?:como\\s+voc[eê]\\s+(?:foi|[eé]|funciona)|how\\s+(?:were\\s+you|are\\s+you)\\s+(?:programm?ed|configured|trained|built|designed))"),

            // Encoded/obfuscated text (excessive unicode)
            ci("[\\x{200B}\\x{200C}\\x{200D}\\x{FEFF}\\x{00AD}]{2,}"),

            // Base64-looking long strings (potential hidden instructions)
            ci("[A-Za-z0-9+/=]{100,}"));

    /**
     * Analyzes a user mensagem for prompt injection attempts.
     */
    public GuardResult analyze(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            return new GuardResult(ThreatLevel.SAFE, null);
        }

        // Normalize: lowercase, collapse whitespace, strip zero-width chars
        String normalized = normalize(mensagem);

        // Check BLOCKED patterns first
        for (Pattern pattern : BLOCKED_PATTERNS) {
            if (pattern.matcher(normalized).find()) {
                String reason = "Blocked pattern detected: "
                        + pattern.pattern().substring(0, Math.min(60, pattern.pattern().length()));
                log.warn("ðŸ›¡ï¸ PROMPT INJECTION BLOCKED â€” reason: {}, mensagem: [{}]",
                        reason, truncate(mensagem, 120));
                return new GuardResult(ThreatLevel.BLOCKED, reason);
            }
        }

        // Check SUSPICIOUS patterns
        for (Pattern pattern : SUSPICIOUS_PATTERNS) {
            if (pattern.matcher(normalized).find()) {
                String reason = "Suspicious pattern: "
                        + pattern.pattern().substring(0, Math.min(60, pattern.pattern().length()));
                log.info("âš ï¸ SUSPICIOUS MESSAGE â€” reason: {}, mensagem: [{}]",
                        reason, truncate(mensagem, 120));
                return new GuardResult(ThreatLevel.SUSPICIOUS, reason);
            }
        }

        return new GuardResult(ThreatLevel.SAFE, null);
    }

    // â”€â”€ Helpers â”€â”€

    private static Pattern ci(String regex) {
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    private String normalize(String input) {
        // Remove zero-width characters
        String cleaned = input.replaceAll("[\\x{200B}\\x{200C}\\x{200D}\\x{FEFF}\\x{00AD}]", "");
        // Collapse multiple whitespace into single space
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        // Remove accents for pattern matching (keep original for display)
        return cleaned.toLowerCase();
    }

    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen)
            return text;
        return text.substring(0, maxLen) + "...";
    }
}
