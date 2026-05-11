import React, { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { MessageSquare, X, Send, Bot, User, Loader2, Sparkles, AlertCircle } from 'lucide-react';
import { aiService } from '../api/aiService';
import { cn } from '../lib/utils';
import ReactMarkdown from 'react-markdown';

export function AiChat() {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    { id: 1, role: 'assistant', content: 'Merhaba! Ben Dava Asistanınız. Size nasıl yardımcı olabilirim?' }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [currentStreamedMessage, setCurrentStreamedMessage] = useState('');
  const scrollRef = useRef(null);

  // Auto-scroll to bottom when messages change or streaming happens
  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages, currentStreamedMessage, isLoading]);

  const handleSend = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage = { id: Date.now(), role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    const promptText = input;
    setInput('');
    setIsLoading(true);
    setCurrentStreamedMessage('');

    try {
      let accumulatedResponse = '';
      await aiService.chatStream(promptText, (chunk) => {
        accumulatedResponse += chunk;
        setCurrentStreamedMessage(accumulatedResponse);
      });

      // Once streaming is finished, add the complete message to the list
      setMessages(prev => [...prev, { 
        id: Date.now() + 1, 
        role: 'assistant', 
        content: accumulatedResponse 
      }]);
      setCurrentStreamedMessage('');
    } catch (error) {
      setMessages(prev => [...prev, { 
        id: Date.now() + 1, 
        role: 'assistant', 
        content: 'Üzgünüm, şu an bağlantı kuramıyorum. Lütfen sistem yöneticinizle iletişime geçin.' 
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="fixed bottom-6 right-6 z-50">
      {/* Floating Button */}
      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={() => setIsOpen(!isOpen)}
        className={cn(
          "flex h-14 w-14 items-center justify-center rounded-full shadow-lg transition-all duration-300",
          isOpen ? "bg-slate-200 text-slate-600 rotate-90" : "bg-indigo-600 text-white"
        )}
      >
        {isOpen ? <X className="h-6 w-6" /> : <Sparkles className="h-6 w-6" />}
      </motion.button>

      {/* Chat Window */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: 20, scale: 0.95 }}
            animate={{ opacity: 1, y: 0, scale: 1 }}
            exit={{ opacity: 0, y: 20, scale: 0.95 }}
            className="absolute bottom-20 right-0 w-[420px] max-h-[650px] rounded-3xl border border-slate-200 bg-white shadow-2xl overflow-hidden flex flex-col"
          >
            {/* Header */}
            <div className="p-5 border-b bg-indigo-50/50 flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-2xl bg-indigo-600 flex items-center justify-center shadow-md shadow-indigo-100">
                  <Bot className="h-6 w-6 text-white" />
                </div>
                <div>
                  <h3 className="font-bold text-slate-900 text-sm">Hukuk Asistanı</h3>
                  <div className="flex items-center gap-1.5">
                    <span className="h-2 w-2 rounded-full bg-emerald-500 animate-pulse" />
                    <p className="text-[10px] uppercase tracking-wider font-bold text-slate-500">Çevrimiçi</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Messages Area */}
            <div 
              ref={scrollRef}
              className="flex-1 overflow-y-auto p-5 space-y-6 min-h-[350px] max-h-[480px] bg-slate-50/30 scroll-smooth"
            >
              {messages.map((msg) => (
                <div
                  key={msg.id}
                  className={cn(
                    "flex gap-3",
                    msg.role === 'user' ? "flex-row-reverse" : ""
                  )}
                >
                  <div className={cn(
                    "h-8 w-8 rounded-xl flex items-center justify-center shrink-0 shadow-sm",
                    msg.role === 'user' ? "bg-indigo-600 text-white" : "bg-white border border-slate-100 text-indigo-600"
                  )}>
                    {msg.role === 'user' ? <User className="h-4 w-4" /> : <Bot className="h-4 w-4" />}
                  </div>
                  <div className={cn(
                    "p-4 rounded-2xl text-sm leading-relaxed max-w-[80%] shadow-sm",
                    msg.role === 'user' 
                      ? "bg-indigo-600 text-white rounded-tr-none" 
                      : "bg-white border border-slate-100 text-slate-700 rounded-tl-none"
                  )}>
                    <ReactMarkdown className="prose prose-sm max-w-none prose-slate">
                      {msg.content}
                    </ReactMarkdown>
                  </div>
                </div>
              ))}

              {/* Streaming Message */}
              {currentStreamedMessage && (
                <div className="flex gap-3">
                  <div className="h-8 w-8 rounded-xl bg-white border border-slate-100 text-indigo-600 flex items-center justify-center shrink-0 shadow-sm">
                    <Bot className="h-4 w-4" />
                  </div>
                  <div className="p-4 rounded-2xl rounded-tl-none text-sm leading-relaxed max-w-[80%] bg-white border border-slate-100 text-slate-700 shadow-sm">
                    <ReactMarkdown className="prose prose-sm max-w-none prose-slate">
                      {currentStreamedMessage}
                    </ReactMarkdown>
                  </div>
                </div>
              )}

              {isLoading && !currentStreamedMessage && (
                <div className="flex gap-3">
                  <div className="h-8 w-8 rounded-xl bg-white border border-slate-100 text-indigo-600 flex items-center justify-center shrink-0 shadow-sm">
                    <Loader2 className="h-4 w-4 animate-spin" />
                  </div>
                  <div className="p-4 rounded-2xl rounded-tl-none text-sm bg-white border border-slate-100 text-slate-400 italic shadow-sm">
                    Yanıt hazırlanıyor...
                  </div>
                </div>
              )}
            </div>

            {/* Input Area */}
            <div className="p-5 border-t bg-white">
              <div className="relative flex items-center gap-3">
                <input
                  type="text"
                  placeholder="Hukuki bir konuda soru sorun..."
                  value={input}
                  onChange={(e) => setInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSend()}
                  disabled={isLoading}
                  className="w-full bg-slate-50 border border-slate-100 rounded-2xl py-3.5 px-5 pr-14 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:bg-white focus:border-indigo-200 transition-all disabled:opacity-50"
                />
                <button
                  onClick={handleSend}
                  disabled={isLoading || !input.trim()}
                  className="absolute right-2 h-10 w-10 rounded-xl bg-indigo-600 text-white flex items-center justify-center hover:bg-indigo-700 disabled:opacity-40 disabled:hover:bg-indigo-600 transition-all shadow-md shadow-indigo-100"
                >
                  <Send className="h-4.5 w-4.5" />
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
