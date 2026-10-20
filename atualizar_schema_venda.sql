USE TecStore;
GO

-- Remove a FK e a coluna id_item da tabela venda (vinculava a venda a um único item)
DECLARE @nomeFK VARCHAR(200);
SELECT @nomeFK = fk.name
FROM sys.foreign_keys fk
WHERE fk.parent_object_id = OBJECT_ID('dbo.venda')
  AND fk.referenced_object_id = OBJECT_ID('dbo.item_venda');

IF @nomeFK IS NOT NULL
BEGIN
    DECLARE @sql NVARCHAR(200) = N'ALTER TABLE dbo.venda DROP CONSTRAINT [' + @nomeFK + N'];';
    EXEC sp_executesql @sql;
END
GO

IF EXISTS (SELECT 1 FROM sys.columns WHERE object_id = OBJECT_ID('dbo.venda') AND name = 'id_item')
    ALTER TABLE dbo.venda DROP COLUMN id_item;
GO

-- Cria a tabela de associação que permite N itens por venda
IF OBJECT_ID('dbo.venda_item') IS NULL
BEGIN
    CREATE TABLE dbo.venda_item(
        id_venda INT NOT NULL REFERENCES dbo.venda(id_venda),
        id_item INT NOT NULL REFERENCES dbo.item_venda(id_item),
        PRIMARY KEY (id_venda, id_item)
    );
END
GO
