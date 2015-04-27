package org.openmrs.module.mirebalais.radiology;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.OrderContext;
import org.openmrs.api.OrderNumberGenerator;
import org.openmrs.api.OrderService;
import org.openmrs.module.idgen.validator.LuhnMod10IdentifierValidator;
import org.openmrs.module.mirebalais.MirebalaisConstants;
import org.openmrs.module.mirebalais.api.MirebalaisHospitalService;
import org.openmrs.module.radiologyapp.RadiologyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(MirebalaisConstants.RADIOLOGY_ORDER_NUMBER_GENERATOR_BEAN_ID)
public class RadiologyOrderNumberGenerator implements OrderNumberGenerator {

    @Autowired
    private RadiologyProperties radiologyProperties;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MirebalaisHospitalService mirebalaisHospitalService;

    private static final String ORDER_NUMBER_PREFIX = "ORD-";

    @Override
    public String getNewOrderNumber(OrderContext orderContext) {
        if (orderContext.getOrderType() != null && orderContext.getOrderType().equals(radiologyProperties.getRadiologyTestOrderType())) {
            String orderNumber = mirebalaisHospitalService.getNextRadiologyOrderNumberSeedSequenceValue().toString();
            orderNumber =  new LuhnMod10IdentifierValidator().getValidIdentifier(orderNumber);  // add check digit
            return StringUtils.leftPad(orderNumber, 10, "0"); // pad to ten digits
        }
        else {
            // use standard order format
            return ORDER_NUMBER_PREFIX + orderService.getNextOrderNumberSeedSequenceValue();
        }
    }

    // setters to allow injection of mocks
    public void setRadiologyProperties(RadiologyProperties radiologyProperties) {
        this.radiologyProperties = radiologyProperties;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void setMirebalaisHospitalService(MirebalaisHospitalService mirebalaisHospitalService) {
        this.mirebalaisHospitalService = mirebalaisHospitalService;
    }
}
