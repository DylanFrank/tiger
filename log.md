这里是项目修改日志.

**4/17**

- src/lexer/Lexer.java : 采用HashMap<String,ToKen>存放所有token,对输入变量做简单匹配，简化手动编码逻辑，复杂度相对较高，不过鉴于字符较少，这个复杂度影响很小
