package spring.body;
import spring.INodeType;

public class INode {
    private String hdfsPath;
    private String hostPath;
    private INodeType inodeType;

    public String getHostPath() {
        return hostPath;
    }

    public void setHostPath(String hostPath) {
        this.hostPath = hostPath;
    }

    public String getHdfsPath() {
        return hdfsPath;
    }

    public void setHdfsPath(String hdfsPath) {
        this.hdfsPath = hdfsPath;
    }

    public INodeType getInodeType() {
        return inodeType;
    }

    public void setInodeType(INodeType inodeType) {
        this.inodeType = inodeType;
    }
}