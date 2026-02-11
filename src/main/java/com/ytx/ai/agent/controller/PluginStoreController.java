package com.ytx.ai.agent.controller;

import com.ytx.ai.agent.entity.PluginStoreEntity;
import com.ytx.ai.agent.service.PluginStoreService;
import com.ytx.ai.agent.vo.PageSearchVO;
import com.ytx.ai.agent.vo.PageVO;
import com.ytx.ai.agent.vo.PluginVO;
import com.ytx.ai.web.vo.Response;
import com.ytx.ai.web.vo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * 插件商店对外接口控制器
 * 提供分页查询、新增、更新、删除等基础能力
 */
@RestController
@RequestMapping("/plugin-store")
public class PluginStoreController {

    @Autowired
    private PluginStoreService pluginStoreService;

    /**
     * 分页查询插件商店列表
     *
     * @param pageSearchVO 分页查询条件，包含查询条件实体与分页参数
     * @return 分页结果对象，包含当前页数据和总记录数
     */
    @PostMapping("/page")
    public Response<PageVO<PluginVO>> queryPluginStorePage(@RequestBody(required = false) PageSearchVO<PluginStoreEntity> pageSearchVO) {
        // 1. 兜底处理空请求体，保证分页参数可用
        PageSearchVO<PluginStoreEntity> safeSearchVO = Objects.isNull(pageSearchVO) ? new PageSearchVO<>() : pageSearchVO;
        // 2. 调用业务服务完成分页查询
        PageVO<PluginVO> pageResult = pluginStoreService.queryPluginStorePage(safeSearchVO);
        return Response.success(pageResult);
    }

    /**
     * 发布商店插件
     * @param pluginStoreEntity 插件商店实体对象，包含业务字段
     * @return 新增记录ID
     */
    @PostMapping("/publish")
    public Response<Integer> publishPluginStore(@RequestBody PluginStoreEntity pluginStoreEntity) {
        // 1. 参数为空直接返回错误，避免空指针
        if (Objects.isNull(pluginStoreEntity)) {
            return buildErrorResponse("插件信息不能为空");
        }
        // 2. 调用业务服务完成新增
        Integer id = pluginStoreService.addPluginStore(pluginStoreEntity);
        return Response.success(id);
    }

    /**
     * 更新插件商店信息
     *
     * @param pluginStoreEntity 插件商店实体对象，必须包含ID
     * @return 更新后的记录ID
     */
    @PutMapping("/update")
    public Response<Integer> updatePluginStore(@RequestBody PluginStoreEntity pluginStoreEntity) {
        // 1. 参数为空或缺少ID时返回错误，避免无效更新
        if (Objects.isNull(pluginStoreEntity) || Objects.isNull(pluginStoreEntity.getId())) {
            return buildErrorResponse("插件ID不能为空");
        }
        // 2. 调用业务服务完成更新
        Integer id = pluginStoreService.updatePluginStore(pluginStoreEntity);
        return Response.success(id);
    }


    @GetMapping("/get")
    public Response<PluginVO> getPlugin(@RequestParam("id") Integer id){
        PluginVO pluginVO= pluginStoreService.getPluginById(id);
        return Response.success(pluginVO);
    }

    /**
     * 删除插件商店信息
     *
     * @param id 插件商店ID
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    public Response<Boolean> deletePluginStore(@PathVariable("id") Integer id) {
        // 1. 参数为空直接返回错误，避免误删除
        if (Objects.isNull(id)) {
            return buildErrorResponse("插件ID不能为空");
        }
        // 2. 调用业务服务完成删除
        boolean deleteResult = pluginStoreService.deletePluginStore(id);
        if (!deleteResult) {
            // 3. 删除失败返回错误信息，方便调用方识别业务失败
            return buildErrorResponse("删除失败");
        }
        return Response.success(true);
    }

    /**
     * 构建统一错误响应
     *
     * @param msg 错误提示信息，用于返回给调用方
     * @param <T> 响应数据类型
     * @return 统一错误响应对象
     */
    private <T> Response<T> buildErrorResponse(String msg) {
        return new Response<>(Status.SYSTEM_EXCEPTION.getCode(), msg);
    }
}