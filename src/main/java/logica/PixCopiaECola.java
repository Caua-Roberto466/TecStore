package logica;

import java.util.Locale;

/**
 * Monta o código "Pix copia e cola" (BR Code estático no padrão EMV do Banco Central).
 */
public class PixCopiaECola {

    public static String gerar(String chave, String nomeRecebedor, String cidade, double valor, String txid) {
        String conta = campo("00", "br.gov.bcb.pix") + campo("01", chave);

        StringBuilder sb = new StringBuilder();
        sb.append(campo("00", "01"));                                   // versão do payload
        sb.append(campo("26", conta));                                  // dados da conta Pix
        sb.append(campo("52", "0000"));                                 // categoria do comerciante
        sb.append(campo("53", "986"));                                  // moeda: BRL
        sb.append(campo("54", String.format(Locale.US, "%.2f", valor)));// valor
        sb.append(campo("58", "BR"));                                   // país
        sb.append(campo("59", limitar(semAcentos(nomeRecebedor), 25))); // nome do recebedor
        sb.append(campo("60", limitar(semAcentos(cidade), 15)));        // cidade
        sb.append(campo("62", campo("05", limitar(txid, 25))));         // identificador da transação
        sb.append("6304");                                              // CRC16 vem por último

        return sb + crc16(sb.toString());
    }

    private static String campo(String id, String valor) {
        return id + String.format("%02d", valor.length()) + valor;
    }

    private static String limitar(String s, int max) {
        return s.length() > max ? s.substring(0, max) : s;
    }

    private static String semAcentos(String s) {
        String n = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toUpperCase();
    }

    // CRC-16/CCITT-FALSE (polinômio 0x1021, valor inicial 0xFFFF)
    public static String crc16(String texto) {
        int crc = 0xFFFF;
        for (byte b : texto.getBytes(java.nio.charset.StandardCharsets.UTF_8)) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
                crc &= 0xFFFF;
            }
        }
        return String.format("%04X", crc);
    }
}
