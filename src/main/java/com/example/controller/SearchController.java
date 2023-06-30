package com.example.controller;

import com.example.dto.SearchParamDto;
import com.example.dto.UserDto;
import com.example.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * // 主页列表查询 SearchDto
 * post("/sanguo_search2", data);
 * // 历史查询
 * post("/v2/search/goods_history", data);
 * // 历史查询v3：分类别 SearchDto
 * post("/v3/search/goods_history", data);
 * // 数据格式解析
 * post("/v1/search/attribute/decode", data);
 * // 查看摊位详情
 * post("/v1/search/get_stall_detail", data);
 * // 查看商行详情
 * post("/v1/search/get_firm_detail", data);
 * // 查看寄卖详情
 * post("/v1/search/get_consignment_detail", data);
 * // 获取商品历史记录 SearchDto
 * post("/v1/search/goods_history", data);
 * // 新增黑名单接口
 * case 1://商行
 * query = {
 *   blacklist_type:1,
 *   player_id:activeRow.shop_owner_name
 * }
 * case 2://寄卖
 * query = {
 *   blacklist_type:3,
 *   player_id:activeRow.seller
 * }
 * case 3://摊位
 * query = {
 *   blacklist_type:2,
 *   player_id:activeRow.stall_owner_name
 * }
 * .post("/v1/search/blacklist", data);
 * // 移除黑名单接口
 * case 1://商行
 * case 2://摊位
 * case 3://寄卖
 * query = {
 *   blacklist_type:3,
 *   player_id:id
 *}
 * post("/v1/search/remove_blacklist", data);
 * // 查看黑名单接口 无参数
 * get("/v1/search/blacklist", data);
 */
@RestController
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @PostMapping("/login")
    public String Login(@RequestBody UserDto userDto){
        return searchService.login(userDto);
    }

    @GetMapping("/search")
    public String Search(@RequestBody SearchParamDto searchParamDto) throws Exception {
        return searchService.search(searchParamDto);
    }

    /*
    加密
    let param = {
          region_name: this.searchData.regionName,
          order_type: this.searchData.isAsc ? "asc" : "desc", //asc 代表按价格从低到高，desc 代表按价格从高到低
          size: this.searchData.size,
          keyword: this.searchData.keyword,
          lowest_price: parseFloat(this.searchData.lowest_price),
          highest_price: parseFloat(this.searchData.highest_price),
          attributes, //为四个属性框中的值，将不为空的属性框的值构建 attributes 数组，空的属性框忽略
          no_attributes, //如上（不含）
          search_type: this.searchData.isFuzzy ? 1 : 2, //1 代表模糊搜索， 2代表精确搜索
        };

        var encrypted = CryptoJS.AES.encrypt(JSON.stringify(param), key, {
          iv: key,
          mode: CryptoJS.mode.CBC,
          padding: CryptoJS.pad.Pkcs7
        });
        let ciphertext = encrypted.ciphertext.toString(CryptoJS.enc.Hex);

     解密
     json 为返回值
     let encryptedHexStr = CryptoJS.enc.Hex.parse(json.data.toString());
              let srcs = CryptoJS.enc.Base64.stringify(encryptedHexStr);
              let decrypt = CryptoJS.AES.decrypt(srcs, key, { iv: key, mode: CryptoJS.mode.CBC, padding: CryptoJS.pad.Pkcs7 });
              let decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
              let {consignments, firms, stalls} = JSON.parse(decryptedStr);
     密钥和偏移量都是token前16位
     */
    @GetMapping("/search2")
    public String Search(@RequestParam String ciphertext) throws Exception {
        log.info(ciphertext);
        return searchService.search(ciphertext);
    }
}
