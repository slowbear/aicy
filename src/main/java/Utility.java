import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Utility {
  // 字符串转化为字符数组
  public static String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

  // 计算哈希值(使用SHA3-256算法)
  public static String getHash(String origin) {
    try {
      final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
      return bytesToHex(digest.digest(origin.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // 检查用户名是否合法
  static boolean checkUserNameValid(String username) {
    return username.matches("[a-zA-z][0-9A-Za-z_]{0,15}");
  }

  // 检查密码格式是否合法
  static boolean checkPasswordValid(String password) {
    return password.matches("[0-9A-Za-z.]{7,16}");
  }
}
