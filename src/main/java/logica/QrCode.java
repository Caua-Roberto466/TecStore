package logica;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Gerador de QR Code simples (modo byte, correção de erro nível M, versões 1 a 20).
 * Não depende de nenhuma biblioteca externa.
 */
public final class QrCode {
    private static final int VERSAO_MAX = 20;

    // Palavras-código de correção de erro por bloco e número de blocos (nível M), indexados pela versão
    private static final int[] ECC_POR_BLOCO = {-1, 10, 16, 26, 18, 24, 16, 18, 22, 22, 26, 30, 22, 22, 24, 24, 28, 28, 26, 26, 26};
    private static final int[] NUM_BLOCOS = {-1, 1, 1, 1, 2, 2, 4, 4, 4, 5, 5, 5, 8, 9, 9, 10, 10, 11, 13, 14, 16};

    private final int versao;
    private final int tamanho;
    private final boolean[][] modulos;
    private final boolean[][] funcao;

    public QrCode(String texto) {
        byte[] dados = texto.getBytes(StandardCharsets.UTF_8);

        int v = -1;
        for (int i = 1; i <= VERSAO_MAX; i++) {
            int bitsContagem = i <= 9 ? 8 : 16;
            if (4 + bitsContagem + dados.length * 8 <= numPalavrasDados(i) * 8) {
                v = i;
                break;
            }
        }
        if (v < 0) {
            throw new IllegalArgumentException("Texto grande demais para o QR Code");
        }
        versao = v;
        tamanho = versao * 4 + 17;
        modulos = new boolean[tamanho][tamanho];
        funcao = new boolean[tamanho][tamanho];

        // Monta os bits de dados: modo byte + tamanho + conteúdo + terminador + preenchimento
        List<Boolean> bits = new ArrayList<>();
        anexar(bits, 0x4, 4);
        anexar(bits, dados.length, versao <= 9 ? 8 : 16);
        for (byte b : dados) {
            anexar(bits, b & 0xFF, 8);
        }
        int capacidade = numPalavrasDados(versao) * 8;
        anexar(bits, 0, Math.min(4, capacidade - bits.size()));
        anexar(bits, 0, (8 - bits.size() % 8) % 8);
        for (int pad = 0xEC; bits.size() < capacidade; pad ^= 0xEC ^ 0x11) {
            anexar(bits, pad, 8);
        }
        byte[] palavras = new byte[bits.size() / 8];
        for (int i = 0; i < bits.size(); i++) {
            if (bits.get(i)) {
                palavras[i >>> 3] |= 1 << (7 - (i & 7));
            }
        }

        desenharPadroesFuncao();
        desenharPalavras(adicionarEccEIntercalar(palavras));

        // Escolhe a máscara com menor penalidade
        int melhorMascara = 0;
        int menorPenalidade = Integer.MAX_VALUE;
        for (int m = 0; m < 8; m++) {
            aplicarMascara(m);
            desenharBitsFormato(m);
            int p = penalidade();
            if (p < menorPenalidade) {
                menorPenalidade = p;
                melhorMascara = m;
            }
            aplicarMascara(m); // desfaz
        }
        aplicarMascara(melhorMascara);
        desenharBitsFormato(melhorMascara);
    }

    public int getTamanho() {
        return tamanho;
    }

    public boolean getModulo(int x, int y) {
        return x >= 0 && x < tamanho && y >= 0 && y < tamanho && modulos[y][x];
    }

    // ------------------------------------------------------------------
    // Padrões de função
    // ------------------------------------------------------------------
    private void desenharPadroesFuncao() {
        for (int i = 0; i < tamanho; i++) {
            definirFuncao(6, i, i % 2 == 0);
            definirFuncao(i, 6, i % 2 == 0);
        }
        desenharLocalizador(3, 3);
        desenharLocalizador(tamanho - 4, 3);
        desenharLocalizador(3, tamanho - 4);

        int[] pos = posicoesAlinhamento();
        int n = pos.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (!((i == 0 && j == 0) || (i == 0 && j == n - 1) || (i == n - 1 && j == 0))) {
                    desenharAlinhamento(pos[i], pos[j]);
                }
            }
        }
        desenharBitsFormato(0);
        desenharVersao();
    }

    private void desenharBitsFormato(int mascara) {
        int dados = (0 << 3) | mascara; // nível M = 00
        int resto = dados;
        for (int i = 0; i < 10; i++) {
            resto = (resto << 1) ^ ((resto >>> 9) * 0x537);
        }
        int bits = ((dados << 10) | resto) ^ 0x5412;

        for (int i = 0; i <= 5; i++) definirFuncao(8, i, bit(bits, i));
        definirFuncao(8, 7, bit(bits, 6));
        definirFuncao(8, 8, bit(bits, 7));
        definirFuncao(7, 8, bit(bits, 8));
        for (int i = 9; i < 15; i++) definirFuncao(14 - i, 8, bit(bits, i));

        for (int i = 0; i < 8; i++) definirFuncao(tamanho - 1 - i, 8, bit(bits, i));
        for (int i = 8; i < 15; i++) definirFuncao(8, tamanho - 15 + i, bit(bits, i));
        definirFuncao(8, tamanho - 8, true);
    }

    private void desenharVersao() {
        if (versao < 7) return;
        int resto = versao;
        for (int i = 0; i < 12; i++) {
            resto = (resto << 1) ^ ((resto >>> 11) * 0x1F25);
        }
        long bits = ((long) versao << 12) | resto;
        for (int i = 0; i < 18; i++) {
            boolean b = ((bits >>> i) & 1) != 0;
            int a = tamanho - 11 + i % 3;
            int c = i / 3;
            definirFuncao(a, c, b);
            definirFuncao(c, a, b);
        }
    }

    private void desenharLocalizador(int cx, int cy) {
        for (int dy = -4; dy <= 4; dy++) {
            for (int dx = -4; dx <= 4; dx++) {
                int dist = Math.max(Math.abs(dx), Math.abs(dy));
                int x = cx + dx;
                int y = cy + dy;
                if (x >= 0 && x < tamanho && y >= 0 && y < tamanho) {
                    definirFuncao(x, y, dist != 2 && dist != 4);
                }
            }
        }
    }

    private void desenharAlinhamento(int cx, int cy) {
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -2; dx <= 2; dx++) {
                definirFuncao(cx + dx, cy + dy, Math.max(Math.abs(dx), Math.abs(dy)) != 1);
            }
        }
    }

    private void definirFuncao(int x, int y, boolean escuro) {
        modulos[y][x] = escuro;
        funcao[y][x] = true;
    }

    private int[] posicoesAlinhamento() {
        if (versao == 1) return new int[0];
        int n = versao / 7 + 2;
        int passo = (versao * 8 + n * 3 + 5) / (n * 4 - 4) * 2;
        int[] r = new int[n];
        r[0] = 6;
        for (int i = n - 1, pos = tamanho - 7; i >= 1; i--, pos -= passo) {
            r[i] = pos;
        }
        return r;
    }

    // ------------------------------------------------------------------
    // Dados, correção de erro (Reed-Solomon) e posicionamento
    // ------------------------------------------------------------------
    private void desenharPalavras(byte[] dados) {
        int i = 0;
        for (int direita = tamanho - 1; direita >= 1; direita -= 2) {
            if (direita == 6) direita = 5;
            for (int vert = 0; vert < tamanho; vert++) {
                for (int j = 0; j < 2; j++) {
                    int x = direita - j;
                    boolean subindo = ((direita + 1) & 2) == 0;
                    int y = subindo ? tamanho - 1 - vert : vert;
                    if (!funcao[y][x] && i < dados.length * 8) {
                        modulos[y][x] = bit(dados[i >>> 3], 7 - (i & 7));
                        i++;
                    }
                }
            }
        }
    }

    private byte[] adicionarEccEIntercalar(byte[] dados) {
        int numBlocos = NUM_BLOCOS[versao];
        int eccBloco = ECC_POR_BLOCO[versao];
        int brutas = numModulosBrutos(versao) / 8;
        int blocosCurtos = numBlocos - brutas % numBlocos;
        int tamBlocoCurto = brutas / numBlocos;

        byte[][] blocos = new byte[numBlocos][];
        byte[] divisor = divisorRs(eccBloco);
        for (int i = 0, k = 0; i < numBlocos; i++) {
            byte[] dat = Arrays.copyOfRange(dados, k, k + tamBlocoCurto - eccBloco + (i < blocosCurtos ? 0 : 1));
            k += dat.length;
            byte[] bloco = Arrays.copyOf(dat, tamBlocoCurto + 1);
            byte[] ecc = restoRs(dat, divisor);
            System.arraycopy(ecc, 0, bloco, bloco.length - eccBloco, ecc.length);
            blocos[i] = bloco;
        }

        byte[] resultado = new byte[brutas];
        for (int i = 0, k = 0; i < blocos[0].length; i++) {
            for (int j = 0; j < blocos.length; j++) {
                if (i != tamBlocoCurto - eccBloco || j >= blocosCurtos) {
                    resultado[k++] = blocos[j][i];
                }
            }
        }
        return resultado;
    }

    private static int numModulosBrutos(int ver) {
        int r = (16 * ver + 128) * ver + 64;
        if (ver >= 2) {
            int n = ver / 7 + 2;
            r -= (25 * n - 10) * n - 55;
            if (ver >= 7) r -= 36;
        }
        return r;
    }

    private static int numPalavrasDados(int ver) {
        return numModulosBrutos(ver) / 8 - ECC_POR_BLOCO[ver] * NUM_BLOCOS[ver];
    }

    private static byte[] divisorRs(int grau) {
        byte[] r = new byte[grau];
        r[grau - 1] = 1;
        int raiz = 1;
        for (int i = 0; i < grau; i++) {
            for (int j = 0; j < grau; j++) {
                r[j] = (byte) multiplicar(r[j] & 0xFF, raiz);
                if (j + 1 < grau) r[j] ^= r[j + 1];
            }
            raiz = multiplicar(raiz, 0x02);
        }
        return r;
    }

    private static byte[] restoRs(byte[] dados, byte[] divisor) {
        byte[] r = new byte[divisor.length];
        for (byte b : dados) {
            int fator = (b ^ r[0]) & 0xFF;
            System.arraycopy(r, 1, r, 0, r.length - 1);
            r[r.length - 1] = 0;
            for (int i = 0; i < r.length; i++) {
                r[i] ^= multiplicar(divisor[i] & 0xFF, fator);
            }
        }
        return r;
    }

    private static int multiplicar(int x, int y) {
        int z = 0;
        for (int i = 7; i >= 0; i--) {
            z = (z << 1) ^ ((z >>> 7) * 0x11D);
            z ^= ((y >>> i) & 1) * x;
        }
        return z;
    }

    // ------------------------------------------------------------------
    // Máscara e penalidade
    // ------------------------------------------------------------------
    private void aplicarMascara(int m) {
        for (int y = 0; y < tamanho; y++) {
            for (int x = 0; x < tamanho; x++) {
                boolean inverter;
                switch (m) {
                    case 0: inverter = (x + y) % 2 == 0; break;
                    case 1: inverter = y % 2 == 0; break;
                    case 2: inverter = x % 3 == 0; break;
                    case 3: inverter = (x + y) % 3 == 0; break;
                    case 4: inverter = (x / 3 + y / 2) % 2 == 0; break;
                    case 5: inverter = x * y % 2 + x * y % 3 == 0; break;
                    case 6: inverter = (x * y % 2 + x * y % 3) % 2 == 0; break;
                    default: inverter = ((x + y) % 2 + x * y % 3) % 2 == 0; break;
                }
                modulos[y][x] ^= inverter & !funcao[y][x];
            }
        }
    }

    private int penalidade() {
        int total = 0;

        // Regra 1: sequências de 5+ módulos iguais (linhas e colunas)
        for (int a = 0; a < tamanho; a++) {
            int corL = -1, corC = -1, seqL = 0, seqC = 0;
            for (int b = 0; b < tamanho; b++) {
                int l = modulos[a][b] ? 1 : 0;
                int c = modulos[b][a] ? 1 : 0;
                if (l == corL) { seqL++; if (seqL == 5) total += 3; else if (seqL > 5) total++; }
                else { corL = l; seqL = 1; }
                if (c == corC) { seqC++; if (seqC == 5) total += 3; else if (seqC > 5) total++; }
                else { corC = c; seqC = 1; }
            }
        }

        // Regra 2: blocos 2x2 de mesma cor
        for (int y = 0; y < tamanho - 1; y++) {
            for (int x = 0; x < tamanho - 1; x++) {
                boolean c = modulos[y][x];
                if (c == modulos[y][x + 1] && c == modulos[y + 1][x] && c == modulos[y + 1][x + 1]) {
                    total += 3;
                }
            }
        }

        // Regra 3: padrão parecido com o localizador (1011101 com 4 claros de um lado)
        int[] padrao = {1, 0, 1, 1, 1, 0, 1};
        for (int a = 0; a < tamanho; a++) {
            for (int b = 0; b <= tamanho - 7; b++) {
                boolean okL = true, okC = true;
                for (int k = 0; k < 7; k++) {
                    if ((modulos[a][b + k] ? 1 : 0) != padrao[k]) okL = false;
                    if ((modulos[b + k][a] ? 1 : 0) != padrao[k]) okC = false;
                }
                if (okL && (claros(a, b - 4, b, true) || claros(a, b + 7, b + 11, true))) total += 40;
                if (okC && (claros(a, b - 4, b, false) || claros(a, b + 7, b + 11, false))) total += 40;
            }
        }

        // Regra 4: equilíbrio entre módulos escuros e claros
        int escuros = 0;
        for (boolean[] linha : modulos) {
            for (boolean m : linha) if (m) escuros++;
        }
        int totalModulos = tamanho * tamanho;
        int k = (Math.abs(escuros * 20 - totalModulos * 10) + totalModulos - 1) / totalModulos - 1;
        total += Math.max(k, 0) * 10;

        return total;
    }

    // Verifica se o intervalo [de, ate) da linha/coluna 'a' está todo claro (fora da imagem conta como claro)
    private boolean claros(int a, int de, int ate, boolean linha) {
        for (int i = de; i < ate; i++) {
            if (i >= 0 && i < tamanho) {
                boolean m = linha ? modulos[a][i] : modulos[i][a];
                if (m) return false;
            }
        }
        return true;
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------
    private static void anexar(List<Boolean> bits, int valor, int quantidade) {
        for (int i = quantidade - 1; i >= 0; i--) {
            bits.add(((valor >>> i) & 1) != 0);
        }
    }

    private static boolean bit(int valor, int i) {
        return ((valor >>> i) & 1) != 0;
    }
}
