import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { Job } from '@app/core/model/job.interface';
import { UpdateJobRatingRequest } from '@app/core/model/update-job-rating-request.interface';
import { JobService } from '@app/core/services/job.service';
import { NotificationService } from '@app/core/services/notification.service';

@Component({
  selector: 'app-rating',
  imports: [CommonModule, MatIcon, MatIconButton, MatTooltip],
  templateUrl: './rating.component.html',
  styleUrl: './rating.component.scss'
})
export class RatingComponent implements OnInit {
  @Input() job!: Job;
  @Output() ratingChange = new EventEmitter<Job>();

  rating!: number;
  protected color: string = 'primary';
  protected ratingArr = [0, 1, 2, 3, 4];

  constructor(private readonly jobService: JobService, private readonly notificationService: NotificationService) {}

  ngOnInit(): void {
    this.rating = this.job.rating ? this.job.rating.value : 0;
  }

  showIcon(index:number) {
    if (this.rating >= index + 1) {
      return 'star';
    } else {
      return 'star_border';
    }
  }

  updateJobRating(job: Job, newRating: number): void {
    console.log('updating rating');
      // don't reload list as the edited job is replaced after update by the service
      this.jobService.updateJobRating(job.id, { rating: newRating } as UpdateJobRatingRequest).subscribe(
        (j) => {
          console.log('rating changed', j);
          this.job = j;
          this.rating = this.job.rating.value;
          this.ratingChange.emit(j);
          this.notificationService.confirmation($localize`:@@info.job.rating.updated:Rating updated successfully.`);
        }
      );
    }

}
