import { JobsListOptions } from './jobs-list-options';

describe('JobsListOptions', () => {

  it('should initialize with default values', () => {
    const options = new JobsListOptions();
    expect(options.getCurrentPage()).toBe(0);
    expect(options.getItemsPerPage()).toBe(10);
    expect(options.getStatus()).toBeNull();
    expect(options.getStatusMeta()).toBeNull();
    expect(options.getSort()).toBe('createdAt,desc');
    expect(options.getMustReload()).toBeNull();
  });

  it('should change pagination', () => {
    const options = new JobsListOptions();
    options.changePagination(2, 25);
    expect(options.getCurrentPage()).toBe(2);
    expect(options.getItemsPerPage()).toBe(25);
  });

  it('should not change itemsPerPage if null is provided', () => {
    const options = new JobsListOptions();
    options.changePagination(1, null);
    expect(options.getCurrentPage()).toBe(1);
    expect(options.getItemsPerPage()).toBe(10); // default
  });

  it('should apply job status filter', () => {
    const options = new JobsListOptions();
    options.filter('ACTIVE', null);
    expect(options.getStatus()).toBe('ACTIVE');
    expect(options.getStatusMeta()).toBeNull();
  });

  it('should toggle job status filter off when clicked again', () => {
    const options = new JobsListOptions();
    options.filter('ACTIVE', null);
    options.filter('ACTIVE', null);
    expect(options.getStatus()).toBeNull();
  });

  it('should apply job status meta filter', () => {
    const options = new JobsListOptions();
    options.filter(null, 'LATE');
    expect(options.getStatus()).toBeNull();
    expect(options.getStatusMeta()).toBe('LATE');
  });

  it('should toggle job status meta filter off when clicked again', () => {
    const options = new JobsListOptions();
    options.filter(null, 'LATE');
    options.filter(null, 'LATE');
    expect(options.getStatusMeta()).toBeNull();
  });

  it('should set sort order', () => {
    const options = new JobsListOptions();
    options.sort('updatedAt,asc');
    expect(options.getSort()).toBe('updatedAt,asc');
  });

  it('should force reload', () => {
    const options = new JobsListOptions();
    options.forceReload(true);
    expect(options.getMustReload()).toBe(true);
  });

  it('should compare two equal options as equal', () => {
    const a = new JobsListOptions();
    const b = new JobsListOptions();
    expect(a.equals(b)).toBe(true);
  });

  it('should compare two different options as not equal', () => {
    const a = new JobsListOptions();
    const b = new JobsListOptions();
    b.changePagination(1, 20);
    expect(a.equals(b)).toBe(false);
  });

});
