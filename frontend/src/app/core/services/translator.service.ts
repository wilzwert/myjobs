import { Injectable } from '@angular/core';
import { JobStatus } from '../model/job.interface';
import { ActivityType } from '../model/activity-type';

@Injectable({
  providedIn: 'root'
})

export class TranslatorService {
  private errorMessages: Record<string, string> = {
    VALIDATION_FAILED: $localize`:@@error.validation_failed:Validation error`,
    FIELD_CANNOT_BE_EMPTY: $localize`:@@error.field_cannot_be_empty:Field cannot be empty`,
    INVALID_VALUE: $localize`:@@error.invalid_value:Invalid value`,
    INVALID_EMAIL: $localize`:@@error.invalid_email:Invalid email`,
    INVALID_URL: $localize`:@@error.invalid_url:Invalid url`,
    FIELD_TOO_SHORT: $localize`:@@error.field_too_short:Field too short`,
    FIELD_TOO_LONG: $localize`:@@error.field_too_long:Field too long`,
    // we have to use "custome" placeholder like {{min}} that will be handled manually in the translateError method, 
    // because it cannot be handled by angular i18n, as the min value comes from a backend error at runtime
    // and these translations being outside any method, they cannot access any variables
    FIELD_VALUE_TOO_SMALL: $localize `:@@error.field_value_too_small:Value must be at least {{min}}`,
    FIELD_VALUE_TOO_BIG: $localize `:@@error.field_value_too_big:Value must be at most {{max}}`,
    FIELD_MIN_MAX_LENGTH: $localize`:@@error.field_min_max_length:Invalid field size`,
    PAGINATION_INVALID_PAGE: $localize`:@@error.pagination_invalid_page:Invalid page`,
    PAGINATION_INVALID_PAGE_SIZE: $localize`:@@error.pagination_invalid_page_size:Invalid page size`,
    PAGINATION_OFFSET_TOO_BIG: $localize`:@@error.pagination_offset_too_big:Pagination offset too big`,
    UNEXPECTED_ERROR: $localize`:@@error.unexpected_error:An unexpected error occurred`,

    NO_HTML_FETCHER_FOUND: $localize`:@@error.no_html_fetcher_found:HTML could not be fetched`,
    NO_METADATA_EXTRACTOR_FOUND: $localize`:@@error.no_metadata_extractor_found:Job metadata could not be extracted`,

    USER_WEAK_PASSWORD: $localize`:@@error.user_weak_password:Password does not meet requirements`,
    USER_ALREADY_EXISTS: $localize`:@@error.user_already_exists:User already exists`,
    USER_NOT_FOUND: $localize`:@@error.user_not_found:User not found`,
    USER_LOGIN_FAILED: $localize`:@@error.user_login_failed:Login failed`,
    USER_PASSWORD_MATCH_FAILED: $localize`:@@error.user_password_match_failed:Invalid old password`,
    USER_PASSWORD_RESET_EXPIRED: $localize`:@@error.user_password_reset_expired:Reset password token expired`,

    ATTACHMENT_NOT_FOUND: $localize`:@@error.attachment_not_found:Attachment not found`,
    ATTACHMENT_FILE_NOT_READABLE: $localize`:@@error.attachment_file_not_readable:Attachment file not readable`,

    JOB_ALREADY_EXISTS: $localize`:@@error.job_already_exists:Job already exists`,
    JOB_NOT_FOUND: $localize`:@@error.job_not_found:Job not found`
  };

  private jobStatuses: Record<keyof typeof JobStatus, string> = {
    CREATED : $localize `:@@job.status.created:Created`,
    PENDING : $localize `:@@job.status.pending:Pending`,
    RELAUNCHED : $localize `:@@job.status.relaunched:Relaunched`,
    APPLICANT_REFUSED : $localize `:@@job.status.applicant_refused:Refused (by me)`,
    COMPANY_REFUSED : $localize `:@@job.status.company_refused:Refused (by company)`
  };

  private activityTypes: Record<keyof typeof ActivityType, string> = {
    CREATION : $localize `:@@job.activity.creation:Creation`,
    APPLICANT_REFUSAL : $localize `:@@job.activity.applicant_refusal:Refusal (by me)`,
    ATTACHMENT_CREATION : $localize `:@@job.activity.attachment_creation:Attachement creation`,
    ATTACHMENT_DELETION : $localize `:@@job.activity.attachment_deletion:Attachement deletion`,
    APPLICATION : $localize `:@@job.activity.application:Application`,
    RELAUNCH : $localize `:@@job.activity.relaunch:Relaunch`,
    COMPANY_REFUSAL : $localize `:@@job.activity.company_refusal:Refusal (by company)`,
    EMAIL : $localize `:@@job.activity.email:Email`,
    IN_PERSON_INTERVIEW : $localize `:@@job.activity.in_person_interview:In person interview`,
    VIDEO_INTERVIEW : $localize `:@@job.activity.video_interview:Video interview`,
    TEL_INTERVIEW : $localize `:@@job.activity.phone_interview:Phone interview`,
    RATING : $localize `:@@job.activity.rating:Rating`
  }

  translateError(code: string, details: Record<string, string> | null = {}): string {
    const errorMessage = this.errorMessages[code.toUpperCase()];
    if(!errorMessage) {
      return $localize`:@@error.unknown:An unknown error occurred ${code}`;
    }

    if (details !== null) {
      return errorMessage.replace(/{{(\w+)}}/g, (_, key) => details[key] ?? '');
    }
  
    return errorMessage;
  }
  
  translateJobStatus(jobStatus: string) :string {
    const s = jobStatus as keyof typeof JobStatus;
    const status = this.jobStatuses[s];
    return status ?? 'unknown';
  }

  translateActivityType(activityType: string) :string {
    const a = activityType as keyof typeof ActivityType;
    const type = this.activityTypes[a];
    return type ?? 'unknown';
  }
}