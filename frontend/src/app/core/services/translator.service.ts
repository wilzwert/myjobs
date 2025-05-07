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

  translateError(code: string, params: Record<string, any> = {}): string {
    const errorMessage = this.errorMessages[code.toUpperCase()];
    if(errorMessage) {
      return errorMessage;
    }
    return $localize`:@@error.unknown:An unknown error occurred ${code}`;
  }
  
  translateJobStatus(jobStatus: string) :string {
    const s = jobStatus as keyof typeof JobStatus;
    switch(JobStatus[s]) {
      case JobStatus.CREATED :
        return $localize `:@@job.status.created:Created`;
      case JobStatus.PENDING :
        return $localize `:@@job.status.pending:Pending`;
      case JobStatus.RELAUNCHED :
        return $localize `:@@job.status.relaunched:Relaunched`;
      case JobStatus.APPLICANT_REFUSED :
        return $localize `:@@job.status.applicant_refused:Refused (by me)`;
      case JobStatus.COMPANY_REFUSED :
        return $localize `:@@job.status.company_refused:Refused (by company)`;
    }
  }

  translateActivityType(activityType: string) :string {
    const a = activityType as keyof typeof ActivityType;
    switch(ActivityType[a]) {
      case ActivityType.CREATION :
        return $localize `:@@job.activity.creation:Creation`;
      case ActivityType.APPLICANT_REFUSAL :
        return $localize `:@@job.activity.applicant_refusal:Refusal (by me)`;
      case ActivityType.ATTACHMENT_CREATION :
        return $localize `:@@job.activity.attachment_creation:Attachement creation`;
        case ActivityType.ATTACHMENT_DELETION :
        return $localize `:@@job.activity.attachment_deletion:Attachement deletion`;
      case ActivityType.APPLICATION :
        return $localize `:@@job.activity.application:Application`;
      case ActivityType.RELAUNCH :
        return $localize `:@@job.activity.relaunch:Relaunch`;
      case ActivityType.COMPANY_REFUSAL :
        return $localize `:@@job.activity.company_refusal:Refusal (by company)`;
      case ActivityType.EMAIL :
        return $localize `:@@job.activity.email:Email`;
      case ActivityType.IN_PERSON_INTERVIEW :
        return $localize `:@@job.activity.in_person_interview:In person interview`;
      case ActivityType.VIDEO_INTERVIEW :
        return $localize `:@@job.activity.video_interview:Video interview`;
      case ActivityType.TEL_INTERVIEW :
        return $localize `:@@job.activity.phone_interview:Phone interview`;
      case ActivityType.RATING :
        return $localize `:@@job.activity.rating:Rating`;
    }
  }
}