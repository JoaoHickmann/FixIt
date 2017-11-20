package com.fixit;

public class Criptografia {

    private final String alfabetoMatriz = "NhCByfpl#&FEXYaM2*6g@QR$sIjDr-OeKto+Hc7L0nPuG9VbJ8wmTqxi4WkA5ZU3Svd%1z";
    private final String chave = "YPDe6FpaxH&yRNs+jMVB";
    private final String[] alfabetosEixos = {
            "q4f%JmEjrCQkz7&RYMvWNdeg1Hw6c$pA*F+bVl0ULZ3uhOG5@XPt2s#-TaSoyIKx89DnBi",
            "m-dHoQOAVtSJxfi#TgEP&LD2hX1M*weq5nWZG9c$ByRs0kv%46uNK7blzp3CrUYF+j8a@I"
    };
    private String[] matrizLetras;

    public Criptografia(char inicial) {
        inicial = Character.toLowerCase(inicial);
        int tamanho = alfabetoMatriz.length();
        matrizLetras = new String[tamanho];
        int index = alfabetoMatriz.indexOf(inicial);

        String novoAlfabeto = alfabetoMatriz.substring(index) + alfabetoMatriz.substring(0, index);

        for (int i = 0; i < tamanho; i++) {
            matrizLetras[i] = "";
            for (int j = 0; j < tamanho; j++) {
                matrizLetras[i] += novoAlfabeto.charAt(j + i > tamanho - 1 ? j + i - tamanho : j + i);
            }
        }
    }

    public String criptografar(String texto) {
        String textoCriptografado = "";
        int x = 0, y = 0;

        for (int i = 0; i < texto.length(); i++) {
            x = alfabetosEixos[0].indexOf(texto.charAt(i));
            y = alfabetosEixos[1].indexOf(chave.charAt(i));

            textoCriptografado += matrizLetras[y].charAt(x);
        }

        return textoCriptografado;
    }
}
