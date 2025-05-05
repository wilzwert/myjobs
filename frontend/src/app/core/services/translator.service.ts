import { Injectable } from '@angular/core';
import { JobService } from './job.service';
import { JobStatus } from '../model/job.interface';

@Injectable({
  providedIn: 'root'
})

export class TranslatorService {
  translateError(code: string, params: Record<string, any> = {}): string {
    switch (code) {
      case 'VALIDATION_FAILED':
        return $localize`:@@error.validation_failed:Validation error`;
      case 'FIELD_CANNOT_BE_EMPTY':
        return $localize`:@@error.field_cannot_be_empty:Field cannot be empty`;
      case 'INVALID_VALUE':
        return $localize`:@@error.invalid_value:Invalid value`;
      case 'INVALID_EMAIL':
        return $localize`:@@error.invalid_email:Invalid email`;
      case 'INVALID_URL':
        return $localize`:@@error.invalid_url:Invalid url`;
      case 'FIELD_TOO_SHORT':
        return $localize`:@@error.field_too_short:Field too short`;
      case 'FIELD_TOO_LONG':
        return $localize`:@@error.field_too_long:Field too long`;
      case 'FIELD_MIN_MAX_LENGTH':
        return $localize`:@@error.field_min_max_length:Invalid field size`;
      case 'PAGINATION_INVALID_PAGE':
        return $localize`:@@error.pagination_invalid_page:Invalid page`;
      case 'PAGINATION_INVALID_PAGE_SIZE':
        return $localize`:@@error.pagination_invalid_page_size:Invalid page size`;
      case 'PAGINATION_OFFSET_TOO_BIG':
        return $localize`:@@error.pagination_offset_too_big:Pagination offset too big`;

      case 'UNEXPECTED_ERROR':
        return $localize`:@@error.unexpected:An unexpected error occured`;
  
      // fetch / extract parse jobs specific errors
      case 'NO_HTML_FETCHER_FOUND':
        return $localize`:@@error.no_html_fetcher_found:Html could not be fetched`;
      case 'NO_METADATA_EXTRACTOR_FOUND':
        return $localize`:@@error.no_metadata_extractor_found:Job metadata could not be extracted`;
  
  
      // user specific errors
      case 'USER_WEAK_PASSWORD':
        return $localize`:@@error.user_weak_password:Password does not meet requirements`;
      case 'USER_ALREADY_EXISTS':
        return $localize`:@@error.user_already_exists:User already exists`;
      case 'USER_NOT_FOUND':
        return $localize`:@@error.user_not_found:User not found`;
      case 'USER_LOGIN_FAILED':
        return $localize`:@@error.user_login_failed:Login failed`;
      case 'USER_PASSWORD_MATCH_FAILED':
        return $localize`:@@error.user_password_match_failed:Invalid old password`;
      case 'USER_PASSWORD_RESET_EXPIRED':
        return $localize`:@@error.user_password_reset_expired:Reset password token expired`;
  
      // attachment specific errors
      case 'ATTACHMENT_NOT_FOUND':
        return $localize`:@@error.attachment_not_found:Attachment not found`;
      case 'ATTACHMENT_FILE_NOT_READABLE':
        return $localize`:@@error.attachment_file_not_readable:Attachment file not readable`;
  
      // job specific errors
      case 'JOB_ALREADY_EXISTS':
        return $localize`:@@error.job_already_exists:Job already exists`;
      case 'JOB_NOT_FOUND':
        return $localize`:@@error.job_not_found:Job not found`;


      default:
        return $localize`:@@error.unknown:An unknown error occurred ${code}`;
    }
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
}