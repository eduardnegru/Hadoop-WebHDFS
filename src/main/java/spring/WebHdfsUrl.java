package spring;

public class WebHdfsUrl {

    public static class Builder {

        private String host = "172.18.0.2";
        private String port = "9864";
        private String path = "";
        private String method = "LISTSTATUS";
        private String protocol = "http";
        private String homeFolder = "/user/root";
        private String queryParams = "";

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder setHomeFolder(String homeFolder) {
            this.homeFolder = homeFolder;
            return this;
        }

        public Builder setQueryParameters(String queryParameters) {
            this.queryParams = queryParameters;
            return this;
        }

        public WebHdfsUrl build() {
            WebHdfsUrl webHdfsUrl = new WebHdfsUrl();
            webHdfsUrl.host = this.host;
            webHdfsUrl.port = this.port;
            webHdfsUrl.path = this.path;
            webHdfsUrl.method = this.method;
            webHdfsUrl.protocol = this.protocol;
            webHdfsUrl.homeFolder = this.homeFolder;
            webHdfsUrl.queryParams = this.queryParams;
            return webHdfsUrl;
        }
    }

    private String host;
    private String port;
    private String path;
    private String method;
    private String protocol;
    private String homeFolder;
    private String queryParams;
    private static final String webhdfsUrl = "/webhdfs/v1";

    private WebHdfsUrl() {
    }

    //eg http://0562d2963076:9864/webhdfs/v1/user/root/test2?op=CREATE&namenoderpcaddress=namenode:9000&createflag=&createparent=true&overwrite=false
    public String getUrl() {
        System.out.println("PATH=" + path);
        return protocol +  "://" + host + ":" + port + webhdfsUrl + homeFolder + path + "?op=" + method + queryParams;
    }
}
