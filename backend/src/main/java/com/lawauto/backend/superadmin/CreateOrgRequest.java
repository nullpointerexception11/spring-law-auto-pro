package com.lawauto.backend.superadmin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrgRequest(
    @NotBlank(message = "Şirket adı boş olamaz")
    String orgName,

    @NotBlank(message = "Admin adı boş olamaz")
    String adminFullName,

    @NotBlank(message = "Admin e-postası boş olamaz")
    @Email(message = "Geçerli bir e-posta adresi girin")
    String adminEmail,

    @NotBlank(message = "Admin şifresi boş olamaz")
    @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
    String adminPassword
) {}
