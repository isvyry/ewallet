package ua.svyry.ewallet.quartz;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import ua.svyry.ewallet.service.CustomerService;

@Component
@NoArgsConstructor
@Slf4j
public class CustomerEnablingJob extends QuartzJobBean {

    private CustomerService customerService;
    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        int result = customerService.unblockAllBlocked();
        log.info(String.format("Unblocked %s customers", result));
    }
}
