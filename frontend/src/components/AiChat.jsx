import React, { memo, useCallback, useMemo, useState } from 'react';
import { Send, X, Bot, User, Loader2, MessageSquare } from 'lucide-react';
import { aiV2Service } from '@/api/aiV2Service';
import ReactMarkdown from 'react-markdown';
import { motion, AnimatePresence } from 'framer-motion';
import { cn } from '@/lib/utils';

const WELCOME_MESSAGE = {
  id: 'welcome',
  role: 'assistant',
  content: 'Merhaba! Hukuk ile ilgili sorularınızı yanıtlamak için buradayım.',
};

const ChatMessage = memo(function ChatMessage({ message }) {
  const isUser = message.role === 'user';

  return (
    <div className={cn('flex gap-2.5', isUser ? 'justify-end' : '')}>
      {!isUser && (
        <div className="h-7 w-7 rounded-md bg-muted flex items-center justify-center shrink-0 mt-0.5">
          <Bot className="h-3.5 w-3.5 text-muted-foreground" />
        </div>
      )}
      <div
        className={cn(
          'px-3.5 py-2.5 rounded-lg text-sm leading-relaxed max-w-[80%]',
          isUser ? 'bg-primary text-primary-foreground' : 'bg-card border border-border text-foreground'
        )}
      >
        <ReactMarkdown>{message.content}</ReactMarkdown>
      </div>
      {isUser && (
        <div className="h-7 w-7 rounded-md bg-primary flex items-center justify-center shrink-0 mt-0.5">
          <User className="h-3.5 w-3.5 text-primary-foreground" />
        </div>
      )}
    </div>
  );
});

function AiChatComponent() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([WELCOME_MESSAGE]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [conversationId] = useState(() => crypto.randomUUID());

  const canSend = useMemo(() => Boolean(input.trim()) && !isLoading, [input, isLoading]);

  const appendMessage = useCallback((message) => {
    setMessages((prev) => [...prev, message]);
  }, []);

  const handleSend = useCallback(async () => {
    if (!canSend) return;

    const text = input.trim();
    setInput('');
    appendMessage({ id: crypto.randomUUID(), role: 'user', content: text });
    setIsLoading(true);

    try {
      const response = await aiV2Service.chat(text, conversationId);
      const reply = typeof response === 'string' ? response : response.reply || JSON.stringify(response);
      appendMessage({ id: crypto.randomUUID(), role: 'assistant', content: reply });
    } catch {
      appendMessage({
        id: crypto.randomUUID(),
        role: 'assistant',
        content: 'Bağlantı hatası. Lütfen daha sonra tekrar deneyin.',
      });
    } finally {
      setIsLoading(false);
    }
  }, [appendMessage, canSend, conversationId, input]);

  const handleInputKeyDown = useCallback(
    (event) => {
      if (event.key === 'Enter') {
        handleSend();
      }
    },
    [handleSend]
  );

  return (
    <>
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            className="fixed bottom-20 right-6 w-[380px] max-w-[calc(100vw-2rem)] rounded-xl border border-border bg-card shadow-lg flex flex-col z-50 overflow-hidden"
            style={{ height: '520px' }}
          >
            <div className="flex items-center justify-between px-4 py-3 border-b border-border bg-primary/5">
              <div className="flex items-center gap-2.5">
                <div className="h-7 w-7 rounded-md bg-primary flex items-center justify-center">
                  <Bot className="h-3.5 w-3.5 text-primary-foreground" />
                </div>
                <div>
                  <p className="text-sm font-medium text-foreground">Hukuk Asistanı</p>
                  <div className="flex items-center gap-1.5">
                    <span className="h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse" />
                    <span className="text-[10px] text-muted-foreground">Çevrimiçi</span>
                  </div>
                </div>
              </div>
              <button
                type="button"
                onClick={() => setIsOpen(false)}
                className="p-1 rounded-md hover:bg-muted text-muted-foreground transition-colors"
              >
                <X className="h-4 w-4" />
              </button>
            </div>

            <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4 bg-muted/30">
              {messages.map((message) => (
                <ChatMessage key={message.id} message={message} />
              ))}

              {isLoading && (
                <div className="flex gap-2.5">
                  <div className="h-7 w-7 rounded-md bg-muted flex items-center justify-center shrink-0 mt-0.5">
                    <Bot className="h-3.5 w-3.5 text-muted-foreground" />
                  </div>
                  <div className="px-3.5 py-2.5 rounded-lg bg-card border border-border">
                    <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                  </div>
                </div>
              )}
            </div>

            <div className="p-3 border-t border-border bg-card">
              <div className="flex items-center gap-2">
                <input
                  type="text"
                  value={input}
                  onChange={(event) => setInput(event.target.value)}
                  onKeyDown={handleInputKeyDown}
                  placeholder="Hukuki bir soru sorun..."
                  disabled={isLoading}
                  className="flex-1 h-9 px-3 rounded-md border border-input bg-background text-sm focus:outline-none focus:ring-1 focus:ring-ring disabled:opacity-50"
                />
                <button
                  type="button"
                  onClick={handleSend}
                  disabled={!canSend}
                  className="h-9 w-9 rounded-md bg-primary text-primary-foreground hover:bg-primary/90 disabled:opacity-50 transition-colors flex items-center justify-center shrink-0"
                >
                  <Send className="h-3.5 w-3.5" />
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {!isOpen && (
        <motion.button
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          onClick={() => setIsOpen(true)}
          className="fixed bottom-6 right-6 h-12 w-12 rounded-full bg-primary text-primary-foreground shadow-lg hover:bg-primary/90 transition-colors flex items-center justify-center z-50"
          type="button"
        >
          <MessageSquare className="h-5 w-5" />
        </motion.button>
      )}
    </>
  );
}

export default memo(AiChatComponent);
