using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using NUnit.Framework;
using SpriteSheetManager.Converters;

namespace SpriteSheetManagerTests
{
    [TestFixture]
    public class PixiJsConverterTests
    {
        [Test]
        public void JsonContainsMeta_MetaDataSet_InSpriteSheet()
        {
            var converter = new PixiJsConverter();
            var jsonData = @"{
  ""frames"": {
    ""center1"": {
      ""frame"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""center2.png"": {
      ""frame"": {
        ""x"": 8,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""center3.png"": {
      ""frame"": {
        ""x"": 16,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""center4.png"": {
      ""frame"": {
        ""x"": 24,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""bottomright.png"": {
      ""frame"": {
        ""x"": 32,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""bottom.png"": {
      ""frame"": {
        ""x"": 0,
        ""y"": 8,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""bottomleft.png"": {
      ""frame"": {
        ""x"": 8,
        ""y"": 8,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""darkcenter1.png"": {
      ""frame"": {
        ""x"": 16,
        ""y"": 8,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""darkcenter2.png"": {
      ""frame"": {
        ""x"": 24,
        ""y"": 8,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""darkcenter3.png"": {
      ""frame"": {
        ""x"": 32,
        ""y"": 8,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""darkcenter4.png"": {
      ""frame"": {
        ""x"": 0,
        ""y"": 16,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""right.png"": {
      ""frame"": {
        ""x"": 8,
        ""y"": 16,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""blank1.png"": {
      ""frame"": {
        ""x"": 16,
        ""y"": 16,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""left.png"": {
      ""frame"": {
        ""x"": 24,
        ""y"": 16,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""blank2.png"": {
      ""frame"": {
        ""x"": 32,
        ""y"": 16,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""blank3.png"": {
      ""frame"": {
        ""x"": 0,
        ""y"": 24,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""blank4.png"": {
      ""frame"": {
        ""x"": 8,
        ""y"": 24,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""blank5.png"": {
      ""frame"": {
        ""x"": 16,
        ""y"": 24,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""topright.png"": {
      ""frame"": {
        ""x"": 24,
        ""y"": 24,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""top.png"": {
      ""frame"": {
        ""x"": 32,
        ""y"": 24,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    },
    ""topleft.png"": {
      ""frame"": {
        ""x"": 0,
        ""y"": 32,
        ""w"": 8,
        ""h"": 8
      },
      ""rotated"": false,
      ""trimmed"": false,
      ""spriteSourceSize"": {
        ""x"": 0,
        ""y"": 0,
        ""w"": 8,
        ""h"": 8
      },
      ""sourceSize"": {
        ""w"": 8,
        ""h"": 8
      }
    }
  },
  ""meta"": {
    ""app"": ""https://github.com/piskelapp/piskel/"",
    ""version"": ""1.0"",
    ""image"": ""darkdirt.png"",
    ""format"": ""RGBA8888"",
    ""size"": {
      ""w"": 40,
      ""h"": 40
    }
  }
}";

            var spriteSheet = converter.FromString(jsonData);

            Assert.That(spriteSheet.ImageFileName, Is.EqualTo("darkdirt.png"));
            Assert.That(spriteSheet.Application, Is.EqualTo("https://github.com/piskelapp/piskel/"));
            Assert.That(spriteSheet.Version, Is.EqualTo("1.0"));
            Assert.That(spriteSheet.Format, Is.EqualTo("RGBA8888"));
            Assert.That(spriteSheet.Size, Is.EqualTo(new Size(40.0, 40.0)));
        }
    }
}
