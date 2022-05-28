package com.nbcio.modules.flowable.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import org.jeecg.common.api.vo.Result;
import com.nbcio.modules.flowable.apithird.entity.SysCategory;
import com.nbcio.modules.flowable.apithird.entity.SysRole;
import com.nbcio.modules.flowable.apithird.entity.SysUser;
import com.nbcio.modules.flowable.apithird.service.IFlowThirdService;
import com.nbcio.modules.flowable.domain.dto.FlowProcDefDto;
import com.nbcio.modules.flowable.domain.dto.FlowSaveXmlVo;
import com.nbcio.modules.flowable.service.IFlowDefinitionService;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 工作流定义
 * @Author: nbacheng
 * @Date:   2022-03-29
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "流程定义")
@RestController
@RequestMapping("/flowable/definition")
public class FlowDefinitionController {

    @Autowired
    private IFlowDefinitionService flowDefinitionService;

    @Autowired
    private IFlowThirdService iFlowThirdService;



    @GetMapping(value = "/list")
    @ApiOperation(value = "流程定义列表", response = FlowProcDefDto.class)
    public Result list(@ApiParam(value = "当前页码", required = true) @RequestParam(name="pageNum",defaultValue="1")Integer pageNum,
                           @ApiParam(value = "每页条数", required = true) @RequestParam(name="pageSize",defaultValue="10")Integer pageSize,
                       FlowProcDefDto flowProcDefDto
    ) {
        return Result.OK(flowDefinitionService.list(pageNum, pageSize,flowProcDefDto));
    }


    @ApiOperation(value = "导入流程文件", notes = "上传bpmn20的xml文件")
    @PostMapping("/import")
    public Result importFile(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String category,
                                 MultipartFile file) {
        InputStream in = null;
        try {
            in = file.getInputStream();
            flowDefinitionService.importFile(name, category, in);
        } catch (Exception e) {
            log.error("导入失败:", e);
            return Result.OK(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流出错", e);
            }
        }

        return Result.OK("导入成功");
    }


    @ApiOperation(value = "读取xml文件")
    @GetMapping("/readXml/{deployId}")
    public Result readXml(@ApiParam(value = "流程定义id") @PathVariable(value = "deployId") String deployId) {
        try {
            return flowDefinitionService.readXml(deployId);
        } catch (Exception e) {
            return Result.error("加载xml文件异常");
        }

    }
    @ApiOperation(value = "读取xml文件")
    @GetMapping("/readXmlByDataId/{dataId}")
    public Result readXmlByDataId(@ApiParam(value = "流程定义id") @PathVariable(value = "dataId") String dataId) {
        try {
            return flowDefinitionService.readXmlByDataId(dataId);
        } catch (Exception e) {
            return Result.error("加载xml文件异常");
        }

    }
    
    // add by nbacheng
    @ApiOperation(value = "读取xml文件")
    @GetMapping("/readXmlByName/{processDefinitionName}")
    public Result readXmlByName(@ApiParam(value = "流程定义name") @PathVariable(value = "processDefinitionName") String processDefinitionName) {
        try {
            return flowDefinitionService.readXmlByName(processDefinitionName);
        } catch (Exception e) {
            return Result.error("加载xml文件异常");
        }

    }
    

    @ApiOperation(value = "读取图片文件")
    @GetMapping("/readImage/{deployId}")
    public void readImage(@ApiParam(value = "流程定义id") @PathVariable(value = "deployId") String deployId, HttpServletResponse response) {
        OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(flowDefinitionService.readImage(deployId));
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    @ApiOperation(value = "读取图片文件")
    @GetMapping("/readImageByDataId/{dataId}")
    public void readImageByDataId(@ApiParam(value = "流程数据业务id") @PathVariable(value = "dataId") String dataId, HttpServletResponse response) {
        OutputStream os = null;
        BufferedImage image = null;
        try {
            image = ImageIO.read(flowDefinitionService.readImageByDataId(dataId));
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    @ApiOperation(value = "保存流程设计器内的xml文件")
    @PostMapping("/save")
    public Result save(@RequestBody FlowSaveXmlVo vo) {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(vo.getXml().getBytes(StandardCharsets.UTF_8));
            flowDefinitionService.importFile(vo.getName(), vo.getCategory(), in);
        } catch (Exception e) {
            log.error("导入失败:", e);
            return Result.error(e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error("关闭输入流出错", e);
            }
        }

        return Result.OK("导入成功");
    }


    @ApiOperation(value = "根据流程定义id启动流程实例")
    @PostMapping("/startByProcDefId/{procDefId}")
    public Result startByProcDefId(@ApiParam(value = "流程定义id") @PathVariable(value = "procDefId") String procDefId,
                        @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables) {
    	if (null == variables){
        	variables = new HashMap<>();
        }
        return flowDefinitionService.startProcessInstanceByProcDefId(procDefId, variables);

    }
    @ApiOperation(value = "根据流程定义key启动流程实例")
    @PostMapping("/startByProcDefKey/{procDefKey}")
    public Result startByProcDefKey(@ApiParam(value = "流程定义id") @PathVariable(value = "procDefKey") String procDefKey,
                        @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables) {
        return flowDefinitionService.startProcessInstanceByKey(procDefKey, variables);

    }
    @ApiOperation(value = "根据业务数据Id和服务名启动流程实例")
    @PostMapping("/startByDataId/{dataId}/{serviceName}")
    public Result startByDataId(@ApiParam(value = "流程业务数据id") @PathVariable(value = "dataId") String dataId,
    		                    @ApiParam(value = "流程业务服务名") @PathVariable(value = "serviceName") String serviceName,
                        @ApiParam(value = "变量集合,json对象") @RequestBody Map<String, Object> variables) {
        variables.put("dataId",dataId);
        return flowDefinitionService.startProcessInstanceByDataId(dataId, serviceName, variables);

    }

    @ApiOperation(value = "激活或挂起流程定义")
    @PutMapping(value = "/updateState")
    public Result updateState(@ApiParam(value = "1:激活,2:挂起", required = true) @RequestParam Integer state,
                                  @ApiParam(value = "流程部署ID", required = true) @RequestParam String deployId) {
        flowDefinitionService.updateState(state, deployId);
        return Result.OK("操作成功");
    }

    @ApiOperation(value = "删除流程")
    @DeleteMapping(value = "/delete")
    public Result delete(@ApiParam(value = "流程部署ID", required = true) @RequestParam String deployId) {
        flowDefinitionService.delete(deployId);
        return Result.OK("删除流程成功");
    }
    
    //add by nbahcneg
    @ApiOperation(value = "获取流程所有节点信息")
    @PostMapping(value = "/listallnode")
    public Result listallnode(@ApiParam(value = "流程名称NAME", required = true) @RequestParam String processName) {
    	JSONArray jsonlist = flowDefinitionService.ListAllNode(processName);
        return Result.OK(jsonlist);
    }

    @ApiOperation(value = "指定流程办理人员列表")
    @GetMapping("/userList")
    public Result userList(SysUser user) {
        List<SysUser> list = iFlowThirdService.getAllUser();
        return Result.OK(list);
    }

    @ApiOperation(value = "指定流程办理角色列表")
    @GetMapping("/roleList")
    public Result roleList(SysRole role) {
        List<SysRole> list = iFlowThirdService.getAllRole();
        return Result.OK(list);
    }
    @ApiOperation(value = "指定流程分类信息列表")
    @GetMapping("/categoryList")
    public Result categoryList(SysCategory category) {
        List<SysCategory> list = iFlowThirdService.getAllCategory();
        return Result.OK(list);
    }

}
