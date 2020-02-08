package spring;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.body.INode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "http://localhost:3001")
public class RestApi {

    @RequestMapping(value = "/upload", method = RequestMethod.PUT)
    public ResponseEntity<Object> upload(@RequestBody INode iNode) {

        HttpStatus httpStatus = HttpStatus.OK;
        HashMap<String, String> stats = new HashMap<>();
        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            if(new File(iNode.getHostPath()).isFile()) {
                stats = hdfsInode.upload(iNode.getHostPath());
            } else {
                stats = hdfsInode.uploadDirectory(iNode.getHostPath());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            stats.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Object>(stats, httpStatus);
    }

    @RequestMapping(value = "/mkdir", method = RequestMethod.PUT)
    @CrossOrigin(origins = "http://localhost:3001")
        public ResponseEntity<Object> mkdir(@RequestBody INode iNode) {

        JSONObject entity = new JSONObject();
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            hdfsInode.mkdir();
            entity.put("message", "Directory created on HDFS");
        } catch (Exception exception) {
            exception.printStackTrace();
            entity.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Object>(entity.toMap(), httpStatus);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<Object> download(@RequestBody INode iNode) {

        HttpStatus httpStatus = HttpStatus.OK;
        HashMap<String, String> stats = new HashMap<>();

        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            stats =  hdfsInode.download(iNode.getHostPath());
        } catch (Exception exception) {
            exception.printStackTrace();
            stats.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Object>(stats, httpStatus);
    }

    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public ResponseEntity<Object> read(@RequestBody INode iNode) {

        HttpStatus httpStatus = HttpStatus.OK;
        HashMap<String, String> stats = new HashMap<>();
        String content = "";

        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            content =  hdfsInode.read();
        } catch (Exception exception) {
            exception.printStackTrace();
            stats.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            return new ResponseEntity<Object>(stats, httpStatus);
        }

        return new ResponseEntity<Object>(content, httpStatus);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Object> delete(@RequestParam(name = "recursive") boolean recursive, @RequestBody INode iNode) {

        JSONObject entity = new JSONObject();
        HttpStatus httpStatus = HttpStatus.OK;

        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            hdfsInode.delete(recursive);
            entity.put("message", "Successful");
        } catch (Exception exception) {
            exception.printStackTrace();
            entity.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Object>(entity.toMap(), httpStatus);
    }

    @RequestMapping(value = "/listStatus", method = RequestMethod.POST)
    public ResponseEntity<Object> listStatus(@RequestBody INode iNode) {

        JSONObject entity = new JSONObject();
        HttpStatus httpStatus = HttpStatus.OK;
        JSONArray filesReturnFormat = new JSONArray();

        try {
            HdfsInode hdfsInode = new HdfsInode(iNode.getHdfsPath(), iNode.getInodeType());
            HttpResponse<JsonNode> files = hdfsInode.listStatus();
            JSONArray fileList = files.getBody().getObject().getJSONObject("FileStatuses").getJSONArray("FileStatus");

            for (int i = 0; i < fileList.length(); i++) {
                JSONObject fileObject = fileList.getJSONObject(i);
                JSONObject newFile = new JSONObject();
                newFile.put("name", fileObject.getString("pathSuffix"));
                newFile.put("type", fileObject.getString("type").equals("DIRECTORY") ? "dir" : "file");
                newFile.put("size", fileObject.getInt("length"));
                newFile.put("accessedAt", fileObject.getLong("accessTime"));
                newFile.put("updatedAt", fileObject.getLong("modificationTime"));
                filesReturnFormat.put(newFile);
            }

            entity.put("data", filesReturnFormat);
        } catch (Exception exception) {
            exception.printStackTrace();
            entity.put("message", exception.getMessage());
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Object>(entity.toMap(), httpStatus);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home() {
        return "It works!";
    }
}