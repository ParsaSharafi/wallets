package sharafi.PaymentGateway;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class Config {

    @Value("${wallets.base.url}")
    private String baseURL;

    @Value("${ftp.host}")
    private String ftpHost;

    @Value("${ftp.user}")
    private String ftpUser;

    @Value("${ftp.password}")
    private String ftpPassword;

    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(baseURL).build();
    }

    @Bean
    public ChannelSftp channelSftp() throws JSchException {
        JSch jsch = new JSch();

        Session session = jsch.getSession(ftpUser, ftpHost, 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(ftpPassword);
        session.connect();

        ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        return channelSftp;
    }
}
