package com.lawauto.backend.ai.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DATA MASKING (Veri Maskeleme) Servisi.
 * 
 * GÜVENLİK: Avukatın girdiği özel verileri AI'ya göndermeden önce maskeleyin.
 * 
 * NEDEN?
 * - AI modelleri (özellikle bulut tabanlı) verileri işlerken loglayabilir
 * - Müvekkil gizliliği korunmalıdır (Avukatlık Kanunu, KVKK, GDPR)
 * - Hassas verilerin 3. parti API'lere gitmesi risk oluşturur
 * 
 * STRATEJİ:
 * 1. AI'ya göndermeden önce maskeli veri gönder
 * 2. AI'nın sadece ilgili kısmı kullanmasını sağla
 * 3. Maskelenmiş verileri aynen koru (içerik değişmez)
 */
@Service
public class DataMaskingService {

    private static final Logger log = LoggerFactory.getLogger(DataMaskingService.class);

    // === PATTERN'LER ===
    
    // TC Kimlik Numarası (11 hane)
    private static final Pattern TCKN_PATTERN = Pattern.compile("\\b[1-9]\\d{10}\\b");
    
    // Telefon numarası (Türkiye formatları)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "(?:\\+?90|0)\\s*[\\s.-]*[0-9]{3}\\s*[\\s.-]*[0-9]{3}\\s*[\\s.-]*[0-9]{2}\\s*[\\s.-]*[0-9]{2}"
    );
    
    // E-posta
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
    );
    
    // Adres (sokak, cadde, mahalle, no)
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(
        "\\b(?:[A-Za-zçğıöşüÇĞİÖŞÜ]+\\s+)?(?:Mah\\.?|Cad\\.?|Sok\\.?|Sokak|Cadde|Mahalle|Bulvarı|Caddesi|Sokağı|No:?\\s*\\d+)\\b.*?(?=\\s*(?:İstanbul|Ankara|İzmir|Bursa|Adana|Antalya|Konya|Mersin|Gaziantep|Diyarbakır|Eskişehir|Kayseri|Samsun|Trabzon|Erzurum|Malatya|Elazığ|Van|Şanlıurfa|Mardin|Hatay|Sivas|Kocaeli|Sakarya|Balıkesir|Denizli|Manisa|Aydın|Tekirdağ|Edirne|Kırklareli|Çanakkale|Muğla|Isparta|Burdur|Uşak|Afyon|Kütahya|Eskişehir|Bilecik|Bolu|Düzce|Zonguldak|Karabük|Bartın|Kastamonu|Sinop|Çorum|Yozgat|Kırşehir|Nevşehir|Niğde|Aksaray|Kırıkkale|Ankara|Konya|Karaman|Mersin|Adana|Osmaniye|Hatay|Kahramanmaraş|Malatya))"
    );
    
    // Vergi numarası (10 hane)
    private static final Pattern TAX_NUMBER_PATTERN = Pattern.compile("\\b\\d{10}\\b(?=\\s*(?:Vergi|vkn|tax))");

    // İsim (basit isim-soyisim maskesi)
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "(?:(?:Sn\\.|Sayın|Av\\.|Dr\\.)?\\s*[A-ZÇĞİÖŞÜ][a-zçğıöşü]+\\s+[A-ZÇĞİÖŞÜ][a-zçğıöşü]+(?:\\s+[A-ZÇĞİÖŞÜ][a-zçğıöşü]+)?)"
    );

    /**
     * Metni maskeleyerek döndürür.
     * Orijinal format korunur, sadece hassas kısımlar maskelenir.
     * 
     * @param text Maskelenecek metin
     * @param level Maskeleme seviyesi
     * @return Maskelenmiş metin
     */
    public String maskData(String text, MaskLevel level) {
        if (text == null || text.isBlank()) return text;
        
        String masked = text;
        
        // TCKN maskele
        masked = maskPattern(masked, TCKN_PATTERN, level.tcknMask());
        
        // Telefon maskele
        masked = maskPattern(masked, PHONE_PATTERN, level.phoneMask());
        
        // E-posta maskele
        masked = maskPattern(masked, EMAIL_PATTERN, level.emailMask());
        
        // Vergi numarası maskele
        masked = maskPattern(masked, TAX_NUMBER_PATTERN, level.taxMask());
        
        log.debug("Veri maskelendi: {} karakter -> {} karakter", text.length(), masked.length());
        return masked;
    }

    /**
     * AI cevabındaki maskelenmiş verileri orijinal haliyle değiştirir.
     * Bu method, AI cevabı kullanıcıya gösterilmeden önce çağrılır.
     * 
     * NOT: Bu method, maskelenmiş verileri eski haline getirmez;
     * sadece AI'nın maskelenmiş verilerle oluşturduğu cevabı temizler.
     */
    public String unmaskForDisplay(String maskedText) {
        // AI cevabındaki "[MASKELENMIŞ]" ibarelerini kaldırma
        return maskedText.replaceAll("\\[MASKELENMİŞ\\]", "[GİZLİ]");
    }

    /**
     * Hassas veriyi tespit et ve sadece gerekli kısmı AI'ya gönder.
     * AI sadece ihtiyacı olan veriyi görür.
     */
    public String prepareForAi(String text, MaskLevel level) {
        // Hassas verileri maskeli olarak gönder
        String masked = maskData(text, level);
        
        // AI'ya göndermeden önce log (sadece maskelenmiş halini logla)
        log.debug("AI'ya gönderilen veri (maskeli): {}", masked.substring(0, Math.min(100, masked.length())));
        
        return masked;
    }

    private String maskPattern(String text, Pattern pattern, String maskFormat) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        
        while (matcher.find()) {
            String original = matcher.group();
            String masked = applyMaskFormat(original, maskFormat);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(masked));
        }
        matcher.appendTail(sb);
        
        return sb.toString();
    }

    private String applyMaskFormat(String original, String maskFormat) {
        if (maskFormat == null || maskFormat.isEmpty()) {
            return original;
        }
        
        // İlk karakteri göster, kalanı mask formatındaki karakterle değiştir
        char maskChar = maskFormat.charAt(0);
        
        if (original.length() <= 3) {
            // Kısa veriler (isim gibi): ortayı maskele
            return original.charAt(0) + 
                   String.valueOf(maskChar).repeat(Math.max(0, original.length() - 2)) + 
                   original.charAt(original.length() - 1);
        }
        
        // Normal: ilk 2 ve son 2 karakteri göster
        return original.substring(0, 2) + 
               String.valueOf(maskChar).repeat(Math.max(0, original.length() - 4)) + 
               original.substring(original.length() - 2);
    }

    /**
     * Maskeleme seviyeleri.
     */
    public enum MaskLevel {
        MINIMAL("*", "*", "@", "*"),          // Sadece çok hassas verileri maskele
        STANDARD("*", "*", "@", "*"),         // Standart maskeleme (varsayılan)
        STRICT("█", "█", "█", "█"),          // Tüm verileri tam gizle
        NONE(null, null, null, null);         // Maskeleme yok
        // (sadece test/development için)

        private final String tcknMask;
        private final String phoneMask;
        private final String emailMask;
        private final String taxMask;

        MaskLevel(String tcknMask, String phoneMask, String emailMask, String taxMask) {
            this.tcknMask = tcknMask;
            this.phoneMask = phoneMask;
            this.emailMask = emailMask;
            this.taxMask = taxMask;
        }

        public String tcknMask() { return tcknMask; }
        public String phoneMask() { return phoneMask; }
        public String emailMask() { return emailMask; }
        public String taxMask() { return taxMask; }
    }
}
