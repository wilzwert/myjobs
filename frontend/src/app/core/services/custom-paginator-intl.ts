import {Injectable} from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import {Subject} from 'rxjs';

import '@angular/localize/init';

@Injectable()
export class CustomPaginatorIntl implements MatPaginatorIntl {
  changes = new Subject<void>();

  firstPageLabel = $localize`:@@pagination.first_page_label:First page`;
  itemsPerPageLabel = $localize`:@@pagination.items_per_page_label:Items per page`;
  lastPageLabel = $localize`:@@pagination.last_page_label:Last page`;

  // You can set labels to an arbitrary string too, or dynamically compute
  // it through other third-party internationalization libraries.
  nextPageLabel = $localize`:@@pagination.next_page_label:Next page`;
  previousPageLabel = $localize`:@@pagination.previous_page_label:Previous page`;

  getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length === 0) {
      return $localize`:@@pagination_page_one_of_one:Page 1 of 1`;
    }
    const amountPages = Math.ceil(length / pageSize);
    return $localize`:@@pagination_range_label:Page ${page + 1} of ${amountPages}`;
  }
}
