package utils;

public class PasswordHash {
    // SHA-256 Constants (first 32 bits of the fractional parts of the cube roots of
    // the first 64 primes)
    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
    };

    // Initial hash values (first 32 bits of the fractional parts of the square
    // roots of the first 8 primes)
    private static final int[] H0 = {
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
            0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19
    };

    /**
     * Compute SHA-256 hash of input string
     *
     * @param input The string to hash
     * @return Hexadecimal representation of the hash
     */
    public static String hash(String input) {
        byte[] message = input.getBytes();
        byte[] paddedMessage = padMessage(message);
        int[] hash = processMessage(paddedMessage);
        return toHexString(hash);
    }

    /**
     * Pad the message according to SHA-256 specification
     */
    private static byte[] padMessage(byte[] message){
        long messageLenBits = (long) message.length * 8;

        //Calculate Padding Length
        int paddingLength = (message.length % 64 < 56) ? (56 - message.length % 64): (120 - message.length % 64);

        byte[] paddedMessage = new byte[message.length + paddingLength + 8];

        System.arraycopy(message, 0, paddedMessage, 0, message.length);

        paddedMessage[message.length] = (byte) 0x80;

        for(int i = 0; i < 8; i++)
            paddedMessage[paddedMessage.length - i - 1] = (byte) (messageLenBits >> (i * 8));

        return paddedMessage;
    }

    /**
     * Right rotate operation
     */
    private static int rightRotate(int value, int bits) {
        return (value >>> bits) | (value << (32 - bits));
    }

    /**
     * Process the padded message
     */
    private static int[] processMessage(byte[] paddedMessage){
        int[] hash = new int[8];
        System.arraycopy(H0, 0, hash, 0, 8);

        // Process message in 512-bit (64-byte) chunks
        for(int chunkStart = 0; chunkStart < paddedMessage.length; chunkStart += 64){
            int[] w = new int[64];

            // Break chunk into sixteen 32-bit big-endian words
            for(int i = 0; i < 16; i++)
                w[i] = ((paddedMessage[chunkStart + i * 4] & 0xFF) << 24) |
                        ((paddedMessage[chunkStart + i * 4 + 1] & 0xFF) << 16) |
                        ((paddedMessage[chunkStart + i * 4 + 2] & 0xFF) << 8) |
                        (paddedMessage[chunkStart + i * 4 + 3] & 0xFF);

            // Extend the sixteen 32-bit words into sixty-four 32-bit words
            for (int i = 16; i < 64; i++) {
                int s0 = rightRotate(w[i - 15], 7) ^ rightRotate(w[i - 15], 18) ^ (w[i - 15] >>> 3);
                int s1 = rightRotate(w[i - 2], 17) ^ rightRotate(w[i - 2], 19) ^ (w[i - 2] >>> 10);
                w[i] = w[i - 16] + s0 + w[i - 7] + s1;
            }

            // Initialize working variables
            int a = hash[0];
            int b = hash[1];
            int c = hash[2];
            int d = hash[3];
            int e = hash[4];
            int f = hash[5];
            int g = hash[6];
            int h = hash[7];

            // Compression function main loop
            for (int i = 0; i < 64; i++) {
                int S1 = rightRotate(e, 6) ^ rightRotate(e, 11) ^ rightRotate(e, 25);
                int ch = (e & f) ^ ((~e) & g);
                int temp1 = h + S1 + ch + K[i] + w[i];
                int S0 = rightRotate(a, 2) ^ rightRotate(a, 13) ^ rightRotate(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int temp2 = S0 + maj;

                h = g;
                g = f;
                f = e;
                e = d + temp1;
                d = c;
                c = b;
                b = a;
                a = temp1 + temp2;
            }

            // Add compressed chunk to current hash value
            hash[0] += a;
            hash[1] += b;
            hash[2] += c;
            hash[3] += d;
            hash[4] += e;
            hash[5] += f;
            hash[6] += g;
            hash[7] += h;
        }

        return hash;
    }

    /**
     * Convert hash to hexadecimal string
     */
    private static String toHexString(int[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int h : hash) {
            StringBuilder hex = new StringBuilder(Integer.toHexString(h));
            // Pad with leading zeros if necessary
            while (hex.length() < 8) {
                hex.insert(0, "0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
