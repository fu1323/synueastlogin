// sm4.js - 从webpack里提取的SM4核心，整理成可直接运行的版本
function bytesToBase64(bytes) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    let result = '', i;
    for (i = 0; i < bytes.length - 2; i += 3) {
        result += chars[bytes[i] >> 2];
        result += chars[((bytes[i] & 3) << 4) | (bytes[i+1] >> 4)];
        result += chars[((bytes[i+1] & 15) << 2) | (bytes[i+2] >> 6)];
        result += chars[bytes[i+2] & 63];
    }
    if (i < bytes.length) {
        result += chars[bytes[i] >> 2];
        if (i+1 < bytes.length) {
            result += chars[((bytes[i] & 3) << 4) | (bytes[i+1] >> 4)];
            result += chars[(bytes[i+1] & 15) << 2];
        } else {
            result += chars[(bytes[i] & 3) << 4];
            result += '=';
        }
        result += '=';
    }
    return result;
}

function base64ToBytes(base64) {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    let bytes = [], i;
    base64 = base64.replace(/=+$/, '');
    for (i = 0; i < base64.length; i += 4) {
        const a = chars.indexOf(base64[i]);
        const b = chars.indexOf(base64[i+1]);
        const c = chars.indexOf(base64[i+2]);
        const d = chars.indexOf(base64[i+3]);
        bytes.push((a << 2) | (b >> 4));
        if (base64[i+2] !== undefined) bytes.push(((b & 15) << 4) | (c >> 2));
        if (base64[i+3] !== undefined) bytes.push(((c & 3) << 6) | d);
    }
    return bytes;
}

// ========== ctx 对象 (原来的 o) ==========
function SM4Ctx() {
    this.mode = 1;
    this.isPadding = true;
    this.sk = new Array(32);
}

// ========== SM4 算法 (原来的 a) ==========
function SM4() {
    this.SM4_ENCRYPT = 1;
    this.SM4_DECRYPT = 0;

    const SBOX = [214,144,233,254,204,225,61,183,22,182,20,194,40,251,44,5,43,103,154,118,42,190,4,195,170,68,19,38,73,134,6,153,156,66,80,244,145,239,152,122,51,84,11,67,237,207,172,98,228,179,28,169,201,8,232,149,128,223,148,250,117,143,63,166,71,7,167,252,243,115,23,186,131,89,60,25,230,133,79,168,104,107,129,178,113,100,218,139,248,235,15,75,112,86,157,53,30,36,14,94,99,88,209,162,37,34,124,59,1,33,120,135,212,0,70,87,159,211,39,82,76,54,2,231,160,196,200,158,234,191,138,210,64,199,56,181,163,247,242,206,249,97,21,161,224,174,93,164,155,52,26,85,173,147,50,48,245,140,177,227,29,246,226,46,130,102,202,96,192,41,35,171,13,83,78,111,213,219,55,69,222,253,142,47,3,255,106,114,109,108,91,81,141,27,175,146,187,221,188,127,17,217,92,65,31,16,90,216,10,193,49,136,165,205,123,189,45,116,208,18,184,229,180,176,137,105,151,74,12,150,119,126,101,185,241,9,197,110,198,132,24,240,125,236,58,220,77,32,121,238,95,62,215,203,57,72];
    const FK = [2746333894,1453994832,1736282519,2993693404];
    const CK = [462357,472066609,943670861,1415275113,1886879365,2358483617,2830087869,3301692121,3773296373,4228057617,404694573,876298825,1347903077,1819507329,2291111581,2762715833,3234320085,3705924337,4177462797,337322537,808926789,1280531041,1752135293,2223739545,2695343797,3166948049,3638552301,4110090761,269950501,741554753,1213159005,1684763257];

    this.GET_ULONG_BE = function(e, t) {
        return (255&e[t])<<24|(255&e[t+1])<<16|(255&e[t+2])<<8|255&e[t+3];
    };

    this.PUT_ULONG_BE = function(e, t, n) {
        var r=255&e>>24, i=255&e>>16, o=255&e>>8, a=255&e;
        t[n]   = r>128 ? r-256 : r;
        t[n+1] = i>128 ? i-256 : i;
        t[n+2] = o>128 ? o-256 : o;
        t[n+3] = a>128 ? a-256 : a;
    };

    this.SHL  = function(e, t) { return (4294967295&e)<<t; };
    this.ROTL = function(e, t) { return this.SHL(e,t)|e>>32-t; };

    this.sm4Sbox = function(t) {
        const n = 255&t, r = SBOX[n];
        return r>128 ? r-256 : r;
    };

    this.sm4Lt = function(e) {
        const r = new Array(4), i = new Array(4);
        this.PUT_ULONG_BE(e, r, 0);
        i[0]=this.sm4Sbox(r[0]); i[1]=this.sm4Sbox(r[1]);
        i[2]=this.sm4Sbox(r[2]); i[3]=this.sm4Sbox(r[3]);
        let t = this.GET_ULONG_BE(i, 0);
        return t ^ this.ROTL(t,2) ^ this.ROTL(t,10) ^ this.ROTL(t,18) ^ this.ROTL(t,24);
    };

    this.sm4F = function(e, t, n, r, i) {
        return e ^ this.sm4Lt(t^n^r^i);
    };

    this.sm4CalciRK = function(e) {
        const r = new Array(4), i = new Array(4);
        this.PUT_ULONG_BE(e, r, 0);
        i[0]=this.sm4Sbox(r[0]); i[1]=this.sm4Sbox(r[1]);
        i[2]=this.sm4Sbox(r[2]); i[3]=this.sm4Sbox(r[3]);
        let t = this.GET_ULONG_BE(i, 0);
        return t ^ this.ROTL(t,13) ^ this.ROTL(t,23);
    };

    this.sm4_setkey = function(e, r) {
        const i = new Array(4), o = new Array(36);
        i[0]=this.GET_ULONG_BE(r,0);  i[1]=this.GET_ULONG_BE(r,4);
        i[2]=this.GET_ULONG_BE(r,8);  i[3]=this.GET_ULONG_BE(r,12);
        o[0]=i[0]^FK[0]; o[1]=i[1]^FK[1]; o[2]=i[2]^FK[2]; o[3]=i[3]^FK[3];
        for (let t=0; t<32; t++) {
            o[t+4] = o[t] ^ this.sm4CalciRK(o[t+1]^o[t+2]^o[t+3]^CK[t]);
            e[t] = o[t+4];
        }
    };

    this.sm4_setkey_enc = function(e, t) { e.mode=this.SM4_ENCRYPT; this.sm4_setkey(e.sk, t); };
    this.sm4_setkey_dec = function(e, t) { e.mode=0; this.sm4_setkey(e.sk, t); e.sk=e.sk.reverse(); };

    this.padding = function(e, t) {
        if (null==e) return null;
        let r = null;
        if (t === this.SM4_ENCRYPT) {
            let n = parseInt(16 - e.length%16);
            r = e.slice(0);
            for (let t=0; t<n; t++) r[e.length+t] = n;
        } else {
            let n = e[e.length-1];
            r = e.slice(0, e.length-n);
        }
        return r;
    };

    this.sm4_one_round = function(e, t, n) {
        let r = 0;
        const i = new Array(36);
        i[0]=this.GET_ULONG_BE(t,0); i[1]=this.GET_ULONG_BE(t,4);
        i[2]=this.GET_ULONG_BE(t,8); i[3]=this.GET_ULONG_BE(t,12);
        while (r<32) { i[r+4]=this.sm4F(i[r],i[r+1],i[r+2],i[r+3],e[r]); r++; }
        this.PUT_ULONG_BE(i[35],n,0); this.PUT_ULONG_BE(i[34],n,4);
        this.PUT_ULONG_BE(i[33],n,8); this.PUT_ULONG_BE(i[32],n,12);
    };

    this.sm4_crypt_cbc = function(e, t, n) {
        if (e.isPadding && e.mode===this.SM4_ENCRYPT) n=this.padding(n,this.SM4_ENCRYPT);
        let s=0, u=n.length, c=[];
        if (e.mode===this.SM4_ENCRYPT) {
            for (let a=0; u>0; u-=16) {
                let r=new Array(16), o=new Array(16);
                let i=n.slice(16*a, 16*(a+1));
                for (let s=0; s<16; s++) r[s]=i[s]^t[s];
                this.sm4_one_round(e.sk, r, o);
                t=o.slice(0,16); c=c.concat(o); a++;
            }
        } else {
            let l=[];
            for (let a=0; u>0; u-=16) {
                let r=new Array(16), o=new Array(16);
                let i=n.slice(16*a, 16*(a+1));
                l=i.slice(0,16);
                this.sm4_one_round(e.sk, i, r);
                for (let s=0; s<16; s++) o[s]=r[s]^t[s];
                t=l.slice(0,16); c=c.concat(o); a++;
            }
        }
        let l=c;
        if (e.isPadding && e.mode===this.SM4_DECRYPT) l=this.padding(l,this.SM4_DECRYPT);
        for (s=0; s<l.length; s++) if(l[s]<0) l[s]=l[s]+256;
        return l;
    };
}

// ========== 工具类 (原来的 s) ==========
function SM4Util() {
    this.secretKey = "gesoft01gesoft01";
    this.iv        = "1q2w3e4r5t6y7u8i";

    this.stringToByte = function(e) {
        const t = [];
        for (let i=0; i<e.length; i++) {
            let r = e.charCodeAt(i);
            if      (r>=65536 && r<=1114111) { t.push(r>>18&7|240); t.push(r>>12&63|128); t.push(r>>6&63|128); t.push(63&r|128); }
            else if (r>=2048  && r<=65535)   { t.push(r>>12&15|224); t.push(r>>6&63|128); t.push(63&r|128); }
            else if (r>=128   && r<=2047)    { t.push(r>>6&31|192); t.push(63&r|128); }
            else                              { t.push(255&r); }
        }
        return t;
    };

    this.byteToString = function(e) {
        let t = "", n = e;
        for (let r=0; r<n.length; r++) {
            const e = n[r].toString(2);
            const i = e.match(/^1+?(?=0)/);
            if (i && 8===e.length) {
                const eLen = i[0].length;
                let o = n[r].toString(2).slice(7-eLen);
                for (let t=1; t<eLen; t++) o += n[t+r].toString(2).slice(2);
                t += String.fromCharCode(parseInt(o,2));
                r += eLen-1;
            } else {
                t += String.fromCharCode(n[r]);
            }
        }
        return t;
    };

    this.encryptDefault_CBC = function(e) {
        const n = new SM4(), r = new SM4Ctx();
        r.isPadding = true; r.mode = n.SM4_ENCRYPT;
        const i = this.stringToByte(this.secretKey);
        const s = this.stringToByte(this.iv);
        n.sm4_setkey_enc(r, i);
        const u = n.sm4_crypt_cbc(r, s, this.stringToByte(e));
        return bytesToBase64(u);  // ← 改这里
    };

    this.decryptDefault_CBC = function(e) {
        const n = new SM4(), r = new SM4Ctx();
        r.isPadding = true; r.mode = n.SM4_ENCRYPT;
        const i = this.stringToByte(this.secretKey);
        const s = this.stringToByte(this.iv);
        n.sm4_setkey_dec(r, i);
        const u = n.sm4_crypt_cbc(r, s, base64ToBytes(e));  // ← 改这里
        return this.byteToString(u);
    };

}

// ========== 测试 ==========
// const sm4 = new SM4Util();
//
// const plain = "123456";
// const enc   = sm4.encryptDefault_CBC(plain);
// const dec   = sm4.decryptDefault_CBC(enc);
//
// console.log("明文:   ", plain);
// console.log("加密:   ", enc);
// console.log("解密:   ", dec);
//
// // 测试你的密文
// console.log("解密目标:", sm4.decryptDefault_CBC("rIrjEc1imMRmKiTxrkMQSA=="));
