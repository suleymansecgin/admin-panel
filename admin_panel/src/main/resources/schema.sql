-- Admin Panel Schema Oluşturma
CREATE SCHEMA IF NOT EXISTS admin_panel;

-- Schema'ya yetki verme (eğer admin_user kullanıcısı varsa)
-- GRANT ALL ON SCHEMA admin_panel TO admin_user;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON TABLES TO admin_user;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA admin_panel GRANT ALL ON SEQUENCES TO admin_user;

-- Not: Tablolar ve index'ler Hibernate tarafından otomatik oluşturulacak (ddl-auto=update)
-- Eğer manuel index oluşturmak isterseniz, aşağıdaki gibi IF NOT EXISTS kullanın:
-- CREATE INDEX IF NOT EXISTS idx_tarih ON admin_panel.siparisler (siparis_tarihi);
