package burp.DnsLogModule.ExtensionMethod;

import java.io.PrintWriter;

import com.github.kevinsawicki.http.HttpRequest;

import burp.Bootstrap.YamlReader;

import burp.IBurpExtenderCallbacks;
import burp.DnsLogModule.ExtensionInterface.DnsLogAbstract;

public class JNDIMonitor extends DnsLogAbstract {
    private IBurpExtenderCallbacks callbacks;

    private String dnslogDomainName;

    private String temporaryDomainName;

    private YamlReader yamlReader;

    public JNDIMonitor(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;

        this.yamlReader = YamlReader.getInstance(callbacks);

        this.dnslogDomainName = this.yamlReader.getString("dnsLogModule.jndi_http");; // http，样式：http://192.168.21.136:54099

        this.temporaryDomainName = this.yamlReader.getString("dnsLogModule.jndi_ldap"); // ldap，样式：192.168.21.136:54088

        this.setExtensionName("JNDIMonitor");

        this.init();
    }

    private void init() {
        String url = this.dnslogDomainName + "/?api2=all";
        String userAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";

        HttpRequest request = HttpRequest.get(url);
        request.trustAllCerts();
        request.trustAllHosts();
        request.followRedirects(false);
        request.header("User-Agent", userAgent);
        request.header("Accept", "*/*");
        request.readTimeout(30 * 1000);
        request.connectTimeout(30 * 1000);

        int statusCode = request.code();
        if (statusCode != 200) {
            throw new RuntimeException(
                    String.format(
                            "%s 扩展-访问url-%s, 请检查本机是否可访问 %s",
                            this.getExtensionName(),
                            statusCode,
                            url));
        }
        // 设置 JNDIMonitor 的临时域名（ldap端口）
        this.setTemporaryDomainName(temporaryDomainName);
    }

    @Override
    public String getBodyContent() {
        String url = this.dnslogDomainName + "/?api2=all";
        String userAgent = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";
        HttpRequest request = HttpRequest.get(url);
        request.trustAllCerts();
        request.trustAllHosts();
        request.followRedirects(false);
        request.header("User-Agent", userAgent);
        request.header("Accept", "*/*");
        request.readTimeout(30 * 1000);
        request.connectTimeout(30 * 1000);
        String body = request.body();

        if (!request.ok()) {
            throw new RuntimeException(
                    String.format(
                            "%s 扩展-%s内容有异常,异常内容: %s",
                            this.getExtensionName(),
                            this.dnslogDomainName,
                            body
                    )
            );
        }

        if (body.equals("[]")) {
            return null;
        }
        return body;
    }

    @Override
    public String export() {
        String str1 = String.format("<br/>============dnsLogExtensionDetail============<br/>");
        String str2 = String.format("ExtensionMethod: %s <br/>", this.getExtensionName());
        String str3 = String.format("dnsLogDomainName: %s <br/>", this.dnslogDomainName);
        String str4 = String.format("dnsLogRecordsApi: %s <br/>", this.dnslogDomainName);
        String str5 = String.format("dnsLogTemporaryDomainName: %s <br/>", this.getTemporaryDomainName());
        String str6 = String.format("=====================================<br/>");

        String detail = str1 + str2 + str3 + str4  + str5 + str6;

        return detail;
    }

    @Override
    public void consoleExport() {
        PrintWriter stdout = new PrintWriter(this.callbacks.getStdout(), true);

        stdout.println("");
        stdout.println("===========JNDIMonitor扩展详情===========");
        stdout.println("你好呀~ (≧ω≦*)喵~");
        stdout.println(String.format("被调用的插件: %s", this.getExtensionName()));
        stdout.println(String.format("JNDIMonitor域名: %s", this.dnslogDomainName));
        stdout.println(String.format("JNDIMonitor保存记录的api接口: %s", this.dnslogDomainName + "/?api2=all"));
        stdout.println(String.format("JNDIMonitor临时域名: %s", this.getTemporaryDomainName()));
        stdout.println("===================================");
        stdout.println("");
    }
}
