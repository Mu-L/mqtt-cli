package com.hivemq.cli.utils.broker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CipherSuite {

    private CipherSuite() {
    }

    public static @NotNull List<String> getAllAsString() {
        final List<String> ciphers_1_2 = Arrays.stream(TLS_1_2_Compatible.values())
                .map(TLS_1_2_Compatible::toString)
                .collect(Collectors.toList());
        final List<String> ciphers_1_3 = Arrays.stream(TLS_1_3_Compatible.values())
                .map(TLS_1_3_Compatible::toString)
                .collect(Collectors.toList());
        final ArrayList<String> allCiphers = new ArrayList<>();
        allCiphers.addAll(ciphers_1_2);
        allCiphers.addAll(ciphers_1_3);
        return allCiphers;
    }

    public enum TLS_1_2_Compatible {
        //TLS_RSA_WITH_NULL_MD5("SSL_RSA_WITH_NULL_MD5"),
        //TLS_RSA_WITH_NULL_SHA("SSL_RSA_WITH_NULL_SHA"),
        //TLS_RSA_EXPORT_WITH_RC4_40_MD5("SSL_RSA_EXPORT_WITH_RC4_40_MD5"),
        //TLS_RSA_WITH_RC4_128_MD5("SSL_RSA_WITH_RC4_128_MD5"),
        //TLS_RSA_WITH_RC4_128_SHA("SSL_RSA_WITH_RC4_128_SHA"),
        //TLS_RSA_EXPORT_WITH_DES40_CBC_SHA("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA"),
        //TLS_RSA_WITH_DES_CBC_SHA("SSL_RSA_WITH_DES_CBC_SHA"),
        //TLS_RSA_WITH_3DES_EDE_CBC_SHA("SSL_RSA_WITH_3DES_EDE_CBC_SHA"),
        //TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA"),
        //TLS_DHE_DSS_WITH_DES_CBC_SHA("SSL_DHE_DSS_WITH_DES_CBC_SHA"),
        //TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA"),
        //TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA"),
        //TLS_DHE_RSA_WITH_DES_CBC_SHA("SSL_DHE_RSA_WITH_DES_CBC_SHA"),
        //TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA"),
        //TLS_DH_anon_EXPORT_WITH_RC4_40_MD5("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5"),
        //TLS_DH_anon_WITH_RC4_128_MD5("SSL_DH_anon_WITH_RC4_128_MD5"),
        //TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA"),
        //TLS_DH_anon_WITH_DES_CBC_SHA("SSL_DH_anon_WITH_DES_CBC_SHA"),
        //TLS_DH_anon_WITH_3DES_EDE_CBC_SHA("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA"),
        //TLS_KRB5_WITH_DES_CBC_SHA("TLS_KRB5_WITH_DES_CBC_SHA"),
        //TLS_KRB5_WITH_3DES_EDE_CBC_SHA("TLS_KRB5_WITH_3DES_EDE_CBC_SHA"),
        //TLS_KRB5_WITH_RC4_128_SHA("TLS_KRB5_WITH_RC4_128_SHA"),
        //TLS_KRB5_WITH_DES_CBC_MD5("TLS_KRB5_WITH_DES_CBC_MD5"),
        //TLS_KRB5_WITH_3DES_EDE_CBC_MD5("TLS_KRB5_WITH_3DES_EDE_CBC_MD5"),
        //TLS_KRB5_WITH_RC4_128_MD5("TLS_KRB5_WITH_RC4_128_MD5"),
        //TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA"),
        //TLS_KRB5_EXPORT_WITH_RC4_40_SHA("TLS_KRB5_EXPORT_WITH_RC4_40_SHA"),
        //TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5"),
        //TLS_KRB5_EXPORT_WITH_RC4_40_MD5("TLS_KRB5_EXPORT_WITH_RC4_40_MD5"),
        TLS_RSA_WITH_AES_128_CBC_SHA("TLS_RSA_WITH_AES_128_CBC_SHA"),
        //TLS_DHE_DSS_WITH_AES_128_CBC_SHA("TLS_DHE_DSS_WITH_AES_128_CBC_SHA"),
        TLS_DHE_RSA_WITH_AES_128_CBC_SHA("TLS_DHE_RSA_WITH_AES_128_CBC_SHA"),
        //TLS_DH_anon_WITH_AES_128_CBC_SHA("TLS_DH_anon_WITH_AES_128_CBC_SHA"),
        TLS_RSA_WITH_AES_256_CBC_SHA("TLS_RSA_WITH_AES_256_CBC_SHA"),
        //TLS_DHE_DSS_WITH_AES_256_CBC_SHA("TLS_DHE_DSS_WITH_AES_256_CBC_SHA"),
        TLS_DHE_RSA_WITH_AES_256_CBC_SHA("TLS_DHE_RSA_WITH_AES_256_CBC_SHA"),
        //TLS_DH_anon_WITH_AES_256_CBC_SHA("TLS_DH_anon_WITH_AES_256_CBC_SHA"),
        //TLS_RSA_WITH_NULL_SHA256("TLS_RSA_WITH_NULL_SHA256"),
        TLS_RSA_WITH_AES_128_CBC_SHA256("TLS_RSA_WITH_AES_128_CBC_SHA256"),
        TLS_RSA_WITH_AES_256_CBC_SHA256("TLS_RSA_WITH_AES_256_CBC_SHA256"),
        //TLS_DHE_DSS_WITH_AES_128_CBC_SHA256("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256"),
        //TLS_RSA_WITH_CAMELLIA_128_CBC_SHA("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA"),
        //TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA"),
        //TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA"),
        TLS_DHE_RSA_WITH_AES_128_CBC_SHA256("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256"),
        //TLS_DHE_DSS_WITH_AES_256_CBC_SHA256("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"),
        TLS_DHE_RSA_WITH_AES_256_CBC_SHA256("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256"),
        //TLS_DH_anon_WITH_AES_128_CBC_SHA256("TLS_DH_anon_WITH_AES_128_CBC_SHA256"),
        //TLS_DH_anon_WITH_AES_256_CBC_SHA256("TLS_DH_anon_WITH_AES_256_CBC_SHA256"),
        //TLS_RSA_WITH_CAMELLIA_256_CBC_SHA("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA"),
        //TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA"),
        //TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA"),
        //TLS_PSK_WITH_RC4_128_SHA("TLS_PSK_WITH_RC4_128_SHA"),
        //TLS_PSK_WITH_3DES_EDE_CBC_SHA("TLS_PSK_WITH_3DES_EDE_CBC_SHA"),
        //TLS_PSK_WITH_AES_128_CBC_SHA("TLS_PSK_WITH_AES_128_CBC_SHA"),
        //TLS_PSK_WITH_AES_256_CBC_SHA("TLS_PSK_WITH_AES_256_CBC_SHA"),
        //TLS_RSA_WITH_SEED_CBC_SHA("TLS_RSA_WITH_SEED_CBC_SHA"),
        TLS_RSA_WITH_AES_128_GCM_SHA256("TLS_RSA_WITH_AES_128_GCM_SHA256"),
        TLS_RSA_WITH_AES_256_GCM_SHA384("TLS_RSA_WITH_AES_256_GCM_SHA384"),
        TLS_DHE_RSA_WITH_AES_128_GCM_SHA256("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256"),
        TLS_DHE_RSA_WITH_AES_256_GCM_SHA384("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384"),
        //TLS_DHE_DSS_WITH_AES_128_GCM_SHA256("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256"),
        //TLS_DHE_DSS_WITH_AES_256_GCM_SHA384("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384"),
        //TLS_DH_anon_WITH_AES_128_GCM_SHA256("TLS_DH_anon_WITH_AES_128_GCM_SHA256"),
        //TLS_DH_anon_WITH_AES_256_GCM_SHA384("TLS_DH_anon_WITH_AES_256_GCM_SHA384"),
        //TLS_EMPTY_RENEGOTIATION_INFO_SCSV("TLS_EMPTY_RENEGOTIATION_INFO_SCSV"),
        //TLS_FALLBACK_SCSV("TLS_FALLBACK_SCSV"),
        //TLS_ECDH_ECDSA_WITH_NULL_SHA("TLS_ECDH_ECDSA_WITH_NULL_SHA"),
        //TLS_ECDH_ECDSA_WITH_RC4_128_SHA("TLS_ECDH_ECDSA_WITH_RC4_128_SHA"),
        //TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA"),
        //TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA"),
        //TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA"),
        //TLS_ECDHE_ECDSA_WITH_NULL_SHA("TLS_ECDHE_ECDSA_WITH_NULL_SHA"),
        //TLS_ECDHE_ECDSA_WITH_RC4_128_SHA("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA"),
        //TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA"),
        //TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA"),
        //TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA"),
        //TLS_ECDH_RSA_WITH_NULL_SHA("TLS_ECDH_RSA_WITH_NULL_SHA"),
        //TLS_ECDH_RSA_WITH_RC4_128_SHA("TLS_ECDH_RSA_WITH_RC4_128_SHA"),
        //TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA"),
        //TLS_ECDH_RSA_WITH_AES_128_CBC_SHA("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA"),
        //TLS_ECDH_RSA_WITH_AES_256_CBC_SHA("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA"),
        //TLS_ECDHE_RSA_WITH_NULL_SHA("TLS_ECDHE_RSA_WITH_NULL_SHA"),
        //TLS_ECDHE_RSA_WITH_RC4_128_SHA("TLS_ECDHE_RSA_WITH_RC4_128_SHA"),
        //TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA"),
        TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA"),
        TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA"),
        //TLS_ECDH_anon_WITH_NULL_SHA("TLS_ECDH_anon_WITH_NULL_SHA"),
        //TLS_ECDH_anon_WITH_RC4_128_SHA("TLS_ECDH_anon_WITH_RC4_128_SHA"),
        //TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA"),
        //TLS_ECDH_anon_WITH_AES_128_CBC_SHA("TLS_ECDH_anon_WITH_AES_128_CBC_SHA"),
        //TLS_ECDH_anon_WITH_AES_256_CBC_SHA("TLS_ECDH_anon_WITH_AES_256_CBC_SHA"),
        //TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256"),
        //TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384"),
        //TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256"),
        //TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384"),
        TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256"),
        TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384"),
        //TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256"),
        //TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384"),
        //TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256"),
        //TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384"),
        //TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256"),
        //TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384"),
        TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256"),
        TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384"),
        //TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256"),
        //TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"),
        //TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA"),
        //TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA"),
        TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256("TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256"),
        //TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256("TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256"),
        TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256("TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256");
        //TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256("TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256"),

        private final @NotNull String cipherSuite;

        @Override
        public @NotNull String toString() {
            return cipherSuite;
        }

        TLS_1_2_Compatible(final @NotNull String asString) {
            this.cipherSuite = asString;
        }
    }

    public enum TLS_1_3_Compatible {
        TLS_AES_128_GCM_SHA256("TLS_AES_128_GCM_SHA256"),
        TLS_AES_256_GCM_SHA384("TLS_AES_256_GCM_SHA384"),
        TLS_CHACHA20_POLY1305_SHA256("TLS_CHACHA20_POLY1305_SHA256");
        //TLS_AES_128_CCM_SHA256("TLS_AES_128_CCM_SHA256"),
        //TLS_AES_128_CCM_8_SHA256("TLS_AES_128_CCM_8_SHA256");

        private final @NotNull String cipherSuite;

        @Override
        public @NotNull String toString() {
            return cipherSuite;
        }

        TLS_1_3_Compatible(final @NotNull String asString) {
            this.cipherSuite = asString;
        }
    }
}
