package com.caogen.view;

import com.alibaba.fastjson.JSON;
import com.caogen.core.exception.AppException;
import com.caogen.core.web.BaseController;
import com.caogen.core.web.MsgOut;
import com.caogen.domain.Resource;
import com.caogen.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 菜单相关
 */
@RestController
public class MenuController extends BaseController {

    @Autowired private ResourceService resourceService;

    /**
     *
     * @api {get} /menus 获取菜单列表
     * @apiSampleRequest /menus
     * @apiExample {curl} Example usage:
     *     curl -i http://localhost:8080/menus
     * @apiPermission admin
     *
     * @apiName list
     * @apiGroup Menu
     * @apiVersion 0.1.0
     * @apiDescription 当前登录用户拥有的菜单
     *
     * @apiParam   {int}   page    页码
     * @apiParam   {int}   rows    页大小
     *
     * @apiSuccess {String} code    结果码
     * @apiSuccess {String} msg     消息说明
     * @apiSuccess {String} type    结果类型
     * @apiSuccess {String} title   提示标题
     * @apiSuccess {Object} data    分页数据
     * @apiSuccess {int}    total   总记录数
     *
     * @apiSuccessExample Success-Response:
     * HTTP/1.1 200 OK
     * {
     *     code: '200',
     *     msg:  'success',
     *     total: 1,
     *     data:  {},
     *     type:  'SUCCESS',
     *     title: '成功'
     * }
     *
     * @apiError Menu 对应<code>ID</code>的菜单没有数据
     * @apiErrorExample Error-Response:
     * HTTP/1.1 404 Not Found
     * {
     *     code: '404',
     *     msg:  'User Not Found',
     *     type: 'ERROR',
     *     title: '错误'
     * }
     */
    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    @RolesAllowed({"ROLE_menus:view", "ROLE_root"})
    public String list() {
        LOGGER.error("sessionId = {}", JSON.toJSON(RequestContextHolder.getRequestAttributes().getSessionId()));
        List<Resource> list;
        Collection<GrantedAuthority> grantedAuthorities
                = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
                .getAuthentication().getAuthorities();

        List<String> grant = new ArrayList<>();
        grantedAuthorities.forEach(grantedAuthority -> {
            grant.add(grantedAuthority.getAuthority().replace("ROLE_",""));
        });
        list = resourceService.selectByResourceLink(grant.toArray(new String[0]));
        MsgOut o = MsgOut.success(list);
        o.setError(SecurityContextHolder.getContext().getAuthentication().getName());
        return this.renderJson(o);
    }

    /**
     *
     * @api {post} /menus 创建新菜单
     * @apiName  create
     * @apiHeader {String} access-key Users unique access-key.
     * @apiHeaderExample {json} Header-Example:
     *     {
     *       "Accept-Encoding": "Accept-Encoding: gzip, deflate"
     *     }
     * @apiGroup Menu
     * @apiVersion 0.1.0
     * @apiDescription 创建一个新菜单
     *
     * @apiParam   {String}  name   名称
     * @apiParam   {String}  link   菜单url
     * @apiParam   {Long}    pid    父级菜单ID
     *
     * @apiSuccess {String} code    结果码
     * @apiSuccess {String} msg     消息说明
     * @apiSuccess {String} type    结果类型
     * @apiSuccess {String} title   提示标题
     * @apiSuccess {Object} data    分页数据
     * @apiSuccess {int}    total   总记录数
     *
     * @apiSuccessExample Success-Response:
     * HTTP/1.1 200 OK
     * {
     *     code: '200',
     *     msg:  'success',
     *     total: 1,
     *     data:  {},
     *     type:  'SUCCESS',
     *     title: '成功'
     * }
     *
     * @apiError Menu 对应<code>ID</code>的菜单没有数据
     * @apiErrorExample Error-Response:
     * HTTP/1.1 404 Not Found
     * {
     *     code: '404',
     *     msg:  'User Not Found',
     *     type: 'ERROR',
     *     title: '错误'
     *
     * }
     */
    @RequestMapping(value = "/menus", method = RequestMethod.POST)
    @RolesAllowed({"ROLE_menus:create", "ROLE_root"})
    public String create(Resource resource) {
        MsgOut o;
        List<Resource> list = new ArrayList<>();
        LOGGER.debug(renderJson(resource));
        resourceService.insert(resource);
        list.add(resource);
        o = MsgOut.success(list);
        return this.renderJson(o);
    }

    @RequestMapping(value = "/menus", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_menus:update", "ROLE_root"})
    public String update(@Valid Resource resource) {
        MsgOut o;
        List<Resource> list = new ArrayList<>();
        resourceService.update(resource);
        list.add(resource);
        o = MsgOut.success(list);
        return this.renderJson(o);
    }
    @RolesAllowed({"ROLE_menus:delete", "ROLE_root"})
    @RequestMapping(value = "/menus/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable("id") Long id) {
        MsgOut o;
        resourceService.delete(id);
        o = MsgOut.success();
        return this.renderJson(o);
    }

    @RequestMapping(value = "/menus/{roleId}", method = RequestMethod.GET)
    @RolesAllowed({"ROLE_menus:view", "ROLE_root"})
    public String getMenuByRoleId(@PathVariable("roleId") Long id) {
        List<Resource> list;
        MsgOut o;
        list = resourceService.selectByRoleId(id);
        o = MsgOut.success(list);
        return this.renderJson(o);
    }

    @RequestMapping(value = "/menus/grant")
    @RolesAllowed({"ROLE_menus:grant", "ROLE_root"})
    public String grant(Long id, String mids) {
        MsgOut o;
        resourceService.grant(id, mids);
        o = MsgOut.success();
        return this.renderJson(o);
    }
}
