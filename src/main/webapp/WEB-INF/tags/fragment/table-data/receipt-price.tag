<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="/WEB-INF/pages/fragments/taglib.jspf" %>
<%@ attribute name="receipt" required="true" type="com.epam.finalproject.dto.ReceiptDTO" %>
<td>
    <i>${receipt.totalPrice}</i>
    <i>${receipt.priceCurrency.code}</i>
</td>