/*
Tirar do comentário para criar o banco
CREATE DATABASE TecStore
COLLATE Latin1_100_CI_AS_SC_UTF8;

USE TecStore;

*/

CREATE TABLE usuario(
id_usuario INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
nome VARCHAR(100) NOT NULL,
email VARCHAR(100) NOT NULL UNIQUE,
senha_hash VARCHAR(70) NOT NULL
);

CREATE TABLE Estoque(
id_estoque INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
quantidade_atual VARCHAR(100) NOT NULL,
estoque_minimo INT NOT NULL,
ultima_atualizacao DATETIME DEFAULT GETDATE()
);

CREATE TABLE produto(
id_produto INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
nome_produto VARCHAR(100) NOT NULL,
descricao_produto VARCHAR(255) NOT NULL DEFAULT 'Produto desconhecido',
categoria_produto VARCHAR(50) NOT NULL DEFAULT 'Categoria desconhecida',
preco_produto DECIMAL(6, 2),
id_estoque INT NOT NULL REFERENCES Estoque
);

CREATE TABLE vendedor(
id_vendedor INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
cpf_vendedor VARCHAR(11) NOT NULL UNIQUE,
nome_vendedor VARCHAR(100) NOT NULL,
comissao DECIMAL(9,2) NOT NULL
);

CREATE TABLE item_venda(
id_item INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
quantidade INT NOT NULL,
preco_unitario DECIMAL(6,2) NOT NULL,
subtotal DECIMAL(12,2) NOT NULL,
id_produto INT NOT NULL REFERENCES produto
);

CREATE TABLE venda(
id_venda INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
data_venda DATETIME DEFAULT GETDATE(),
valor_total DECIMAL(6,2) NOT NULL,
forma_pagamento VARCHAR(100) NOT NULL,
id_usuario INT NOT NULL REFERENCES usuario,
id_vendedor INT NOT NULL REFERENCES vendedor,
id_item INT NOT NULL REFERENCES item_venda
);