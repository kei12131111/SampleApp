package com.example.log4jsample;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class HelloWorldTasklet implements Tasklet {

	private Logger log = Logger.getLogger(HelloWorldTasklet.class);
	
	@Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

       
        for (int i = 0; i < 1000; i++){
            log.info("sample");
            Thread.sleep(500);
        }
        
        return RepeatStatus.FINISHED;
    }
}