package com.l2journey.gameserver.network;

import java.util.Map;

/**
 * Interface para pacotes (Client ou Server) que devem ser auditados.
 * Permite a extração dos dados relevantes do pacote sem precisar parsear os bytes crus.
 */
public interface IAuditablePacket
{
	/**
	 * Extrai os dados do pacote para fins de auditoria/log em JSON.
	 * @return Um Map contendo as chaves (ex: "targetId") e os valores.
	 */
	public Map<String, Object> getAuditData();
}
