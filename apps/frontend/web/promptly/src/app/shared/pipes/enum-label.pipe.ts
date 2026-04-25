import { Pipe, PipeTransform } from '@angular/core';
import { enumToLabel } from '../utils/string-utils';

/**
 * Converts a SCREAMING_SNAKE_CASE enum constant to a human-readable label.
 * Usage: {{ 'IN_REVIEW' | enumLabel }}  →  "In Review"
 */
@Pipe({
  name: 'enumLabel',
  standalone: true,
})
export class EnumLabelPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return enumToLabel(value);
  }
}
