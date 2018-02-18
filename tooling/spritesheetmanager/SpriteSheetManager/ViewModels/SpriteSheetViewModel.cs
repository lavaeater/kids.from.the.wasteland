using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Runtime.CompilerServices;
using System.Windows.Media.Imaging;
using Microsoft.Win32;
using SpriteSheetManager.Commands;
using SpriteSheetManager.Converters;
using SpriteSheetManager.Interfaces;
using SpriteSheetManager.ViewModels;

namespace SpriteSheetManagerUI.ViewModels
{
    public class SpriteSheetViewModel : ObservableObject
    {
        private string _baseDir = @"C:\projects\private\kids.from.the.wasteland\src\android\assets\tiles";

        private string _spriteSheetFileName;

        private readonly ConverterService _converterService = new ConverterService();

        private ISpriteSheet _spriteSheet;
        private BitmapImage _spriteSheetImage;
        private SpriteSheetFrameViewModel _selectedFrame;
        public ObservableCollection<SpriteSheetFrameViewModel> Frames { get; set; }
        public SpriteSheetViewModel()
        {
            OpenCommand = new RelayCommand(OnOpen);
            SaveCommand = new RelayCommand(OnSave);
            ExportCommand = new RelayCommand(OnExport);
        }

        private void OnExport()
        {
            _converterService.Converters[ConverterService.TexturePacker].SaveSpriteSheet(SpriteSheet);
        }

        private void OnSave()
        {
            string suggestedFileName = Path.GetFileName(_converterService.Converters[ConverterService.Internal].GetFileName(SpriteSheet)) ?? "somefile.spm";
            var saveFileDialog = new SaveFileDialog()
            {
                Filter = _converterService.Converters[ConverterService.Internal].FileFilter,
                InitialDirectory = _baseDir,
                FileName = suggestedFileName
            };
            if (saveFileDialog.ShowDialog() == true)
            {
                _converterService.GetConverter(Path.GetExtension(saveFileDialog.FileName)).SaveSpriteSheet(SpriteSheet, saveFileDialog.FileName);
            }
        }

        private void OnOpen()
        {
            var dialog = new OpenFileDialog
            {
                Filter = _converterService.LoadableExtensions,
                InitialDirectory = _baseDir
            };

            if (dialog.ShowDialog() == true)
            {
                _spriteSheetFileName = dialog.FileName;
                SpriteSheet = _converterService.GetConverter(Path.GetExtension(_spriteSheetFileName)).ReadSpriteSheet(_spriteSheetFileName);
                SpriteSheetImage = new BitmapImage();
                SpriteSheetImage.BeginInit();
                SpriteSheetImage.UriSource = new Uri(Path.Combine(SpriteSheet.BaseDir, SpriteSheet.ImageFileName));
                SpriteSheetImage.EndInit();
                SetupFrames();
                _baseDir = Path.GetDirectoryName(dialog.FileName);
            }
        }

        public RelayCommand ExportCommand { get; set; }

        public RelayCommand SaveCommand { get; set; }

        public RelayCommand OpenCommand { get; set; }

        private void SetupFrames()
        {
            var list = new List<SpriteSheetFrameViewModel>();
            foreach (var frame in SpriteSheet.Frames)
            {
                var croppedBitmap = new CroppedBitmap();
                croppedBitmap.BeginInit();
                croppedBitmap.Source = SpriteSheetImage;
                croppedBitmap.SourceRect = frame.TextureRegion.ToInt32Rect();
                croppedBitmap.EndInit();

                list.Add(new SpriteSheetFrameViewModel(frame, croppedBitmap));
            }
            Frames = new ObservableCollection<SpriteSheetFrameViewModel>(list);
            RaisePropertyChanged(nameof(Frames));
        }

        public BitmapImage SpriteSheetImage
        {
            get => _spriteSheetImage;
            set
            {
                _spriteSheetImage = value;
                RaisePropertyChanged(nameof(SpriteSheetImage));
            }
        }


        public ISpriteSheet SpriteSheet
        {
            get => _spriteSheet;
            set
            {
                _spriteSheet = value;
                RaisePropertyChanged(nameof(SpriteSheet));
            }
        }

        public SpriteSheetFrameViewModel SelectedFrame
        {
            get => _selectedFrame;
            set
            {
                if (_selectedFrame != null)
                    _selectedFrame.IsEditing = false;
                if (value != null)
                {
                    _selectedFrame = value;
                    _selectedFrame.IsEditing = true;
                    RaisePropertyChanged(nameof(SelectedFrame));
                }
            }
        }

        private double _imageHeight;
        private double _imageWidth;
        private double _imageY;
        private double _imageX;


        protected override void RaisePropertyChanged([CallerMemberName] string propertyName = null)
        {
            switch (propertyName)
            {
                case "SelectedFrame":
                    RecalculateFrameSize();
                    break;
            }
            base.RaisePropertyChanged(propertyName);
        }

        private void RecalculateFrameSize()
        {
            var xScale = _imageWidth / SpriteSheetImage.PixelWidth;
            var yScale = xScale; //Preserve aspect ratio, maybe?
            SelectedFrame?.Rescale(xScale, yScale, _imageX, _imageY);
        }

        internal void ImageSizeChanged(double x, double y, double width, double height)
        {
            _imageX = x;
            _imageY = y;
            _imageWidth = width;
            _imageHeight = height;
            RecalculateFrameSize();
        }
    }
}
