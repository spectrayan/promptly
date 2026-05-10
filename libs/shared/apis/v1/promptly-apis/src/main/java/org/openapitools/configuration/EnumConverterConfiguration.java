package org.openapitools.configuration;

import com.spectrayan.promptly.infrastructure.in.web.dto.ContentFormat;
import com.spectrayan.promptly.infrastructure.in.web.dto.FindingType;
import com.spectrayan.promptly.infrastructure.in.web.dto.OrgRole;
import com.spectrayan.promptly.infrastructure.in.web.dto.ProjectRole;
import com.spectrayan.promptly.infrastructure.in.web.dto.PromptStatus;
import com.spectrayan.promptly.infrastructure.in.web.dto.ScanStatus;
import com.spectrayan.promptly.infrastructure.in.web.dto.Severity;
import com.spectrayan.promptly.infrastructure.in.web.dto.StepAction;
import com.spectrayan.promptly.infrastructure.in.web.dto.UserStatus;
import com.spectrayan.promptly.infrastructure.in.web.dto.WorkflowStatus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 * This class provides Spring Converter beans for the enum models in the OpenAPI specification.
 *
 * By default, Spring only converts primitive types to enums using Enum::valueOf, which can prevent
 * correct conversion if the OpenAPI specification is using an `enumPropertyNaming` other than
 * `original` or the specification has an integer enum.
 */
@Configuration(value = "org.openapitools.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.contentFormatConverter")
    Converter<String, ContentFormat> contentFormatConverter() {
        return new Converter<String, ContentFormat>() {
            @Override
            public ContentFormat convert(String source) {
                return ContentFormat.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.findingTypeConverter")
    Converter<String, FindingType> findingTypeConverter() {
        return new Converter<String, FindingType>() {
            @Override
            public FindingType convert(String source) {
                return FindingType.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.orgRoleConverter")
    Converter<String, OrgRole> orgRoleConverter() {
        return new Converter<String, OrgRole>() {
            @Override
            public OrgRole convert(String source) {
                return OrgRole.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.projectRoleConverter")
    Converter<String, ProjectRole> projectRoleConverter() {
        return new Converter<String, ProjectRole>() {
            @Override
            public ProjectRole convert(String source) {
                return ProjectRole.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.promptStatusConverter")
    Converter<String, PromptStatus> promptStatusConverter() {
        return new Converter<String, PromptStatus>() {
            @Override
            public PromptStatus convert(String source) {
                return PromptStatus.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.scanStatusConverter")
    Converter<String, ScanStatus> scanStatusConverter() {
        return new Converter<String, ScanStatus>() {
            @Override
            public ScanStatus convert(String source) {
                return ScanStatus.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.severityConverter")
    Converter<String, Severity> severityConverter() {
        return new Converter<String, Severity>() {
            @Override
            public Severity convert(String source) {
                return Severity.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.stepActionConverter")
    Converter<String, StepAction> stepActionConverter() {
        return new Converter<String, StepAction>() {
            @Override
            public StepAction convert(String source) {
                return StepAction.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.userStatusConverter")
    Converter<String, UserStatus> userStatusConverter() {
        return new Converter<String, UserStatus>() {
            @Override
            public UserStatus convert(String source) {
                return UserStatus.fromValue(source);
            }
        };
    }
    @Bean(name = "org.openapitools.configuration.EnumConverterConfiguration.workflowStatusConverter")
    Converter<String, WorkflowStatus> workflowStatusConverter() {
        return new Converter<String, WorkflowStatus>() {
            @Override
            public WorkflowStatus convert(String source) {
                return WorkflowStatus.fromValue(source);
            }
        };
    }

}
