const AI_API_KEY = '200c110ee5d648f3a0044ec0837dbb5b.Kob5XYnD5Owvwi7G'

export interface ChatMessage {
  role: 'system' | 'user' | 'assistant'
  content: string
}

const SYSTEM_PROMPT = `你是一位专业的医疗AI助手，隶属于社区医疗服务系统。你的职责是：
1. 根据患者描述的症状，提供初步的医疗参考建议
2. 给出可能的原因分析、自我护理建议和就医建议
3. 在适当时候建议患者使用"在线问诊"功能联系真人医生

注意事项：
- 始终强调你的建议仅供参考，不能替代专业医生的诊断
- 不要开具处方或推荐具体药物剂量
- 对于严重症状，务必建议患者及时就医
- 回答要专业、温暖、易懂
- 使用 markdown 格式使回答更清晰`

export const sendChatMessage = async (messages: ChatMessage[]): Promise<string> => {
  try {
    const response = await fetch('https://open.bigmodel.cn/api/paas/v4/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${AI_API_KEY}`
      },
      body: JSON.stringify({
        model: 'glm-4-flash',
        messages: [
          { role: 'system', content: SYSTEM_PROMPT },
          ...messages
        ],
        temperature: 0.7,
        max_tokens: 1024
      })
    })

    if (!response.ok) {
      const errorData = await response.json().catch(() => null)
      throw new Error(errorData?.error?.message || `请求失败: ${response.status}`)
    }

    const data = await response.json()
    return data.choices?.[0]?.message?.content || '抱歉，AI暂时无法回复，请稍后再试。'
  } catch (error: any) {
    console.error('AI请求失败:', error)
    throw error
  }
}
