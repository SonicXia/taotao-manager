package com.taotao.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.common.utils.JsonUtils;
import com.taotao.mapper.TbItemParamItemMapper;
import com.taotao.pojo.TbItemParamItem;
import com.taotao.pojo.TbItemParamItemExample;
import com.taotao.pojo.TbItemParamItemExample.Criteria;
import com.taotao.service.ItemParamItemService;
/**
 * 商品管理Service
 * @author Administrator
 *
 */
@Service
public class ItemParamItemServiceImpl implements ItemParamItemService {

	@Autowired
	private TbItemParamItemMapper itemParamItemMapper;
	
	@Override
	public String getItemParamByItemId(long itemId) {
		//根据商品Id查询规格参数
		TbItemParamItemExample example = new TbItemParamItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(itemId);
		//执行查询
		List<TbItemParamItem> list = itemParamItemMapper.selectByExampleWithBLOBs(example);
		if(list == null || list.size() == 0){
			return "";
		}
		//取规格参数信息
		TbItemParamItem tbItemParamItem = list.get(0);
		String paramData = tbItemParamItem.getParamData();
		//生成html
		//把规格参数json数据转换为java对象
		List<Map> jsonList = JsonUtils.jsonToList(paramData, Map.class);
		StringBuffer sb = new StringBuffer();
		sb.append("<table width=\"100%\" cellspacing=\"1\" cellpadding=\"0\" border=\"1\" class=\"Ptable\">\n");
		sb.append("  <tbody>\n");
		for(Map m1:jsonList) {
			sb.append("    <tr>\n");
			sb.append("      <th colspan=\"2\" class=\"tdTitle\">"+m1.get("group")+"</th>\n");
			sb.append("    </tr>\n");
			List<Map> list2 = (List<Map>)m1.get("params");
			for(Map m2:list2){
				sb.append("    <tr>\n");
				sb.append("      <td class=\"tdTitle\">"+m2.get("k")+"</td>\n");
				sb.append("      <td>"+m2.get("v")+"</td>\n");
				sb.append("    </tr>\n");
			}
		}
		sb.append("  </tbody>\n");
		sb.append("</table>");		
		return sb.toString();
	}
}
