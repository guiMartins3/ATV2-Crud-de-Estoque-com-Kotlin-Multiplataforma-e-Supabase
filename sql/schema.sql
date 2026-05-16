-- ============================================================
-- ESTOCADÃO — Schema do Supabase
-- Cole este SQL no SQL Editor do Supabase e execute
-- ============================================================

-- Habilita a extensão para gerar UUIDs automaticamente
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ─── TABELA: products ────────────────────────────────────────

CREATE TABLE IF NOT EXISTS products (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR     NOT NULL,
    description TEXT,
    sku         VARCHAR     NOT NULL UNIQUE,
    category    VARCHAR,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── TABELA: stock_items ─────────────────────────────────────

CREATE TABLE IF NOT EXISTS stock_items (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id  UUID        NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity    INTEGER     NOT NULL DEFAULT 0,
    unit_price  DECIMAL     NOT NULL DEFAULT 0,
    location    VARCHAR,
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- ─── VIEW: stock_summary (usada pelo endpoint /stock/summary) ─

CREATE OR REPLACE VIEW stock_summary AS
SELECT
    p.id          AS product_id,
    p.name        AS product_name,
    SUM(s.quantity) AS total_quantity
FROM products p
LEFT JOIN stock_items s ON s.product_id = p.id
GROUP BY p.id, p.name;

-- ─── TRIGGER: atualiza updated_at automaticamente ─────────────

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER products_updated_at
    BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER stock_items_updated_at
    BEFORE UPDATE ON stock_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
